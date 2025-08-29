// auth.service.ts
import { Injectable, NgZone } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserModel } from './stored-user.model';
import { NotificationStore } from '../../notifications/NotificationStore';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiUrl;

  private readonly _isLoggedIn$ = new BehaviorSubject<boolean>(false);
  private readonly _user$ = new BehaviorSubject<UserModel | null>(null);
  private logoutTimerId: any = null;

  readonly isLoggedIn$ = this._isLoggedIn$.asObservable();
  readonly user$ = this._user$.asObservable();

  /** Synchronous snapshots (useful in guards) */
  get isLoggedIn(): boolean { return this._isLoggedIn$.value; }
  get currentUser(): UserModel | null { return this._user$.value; }

  constructor(
    private http: HttpClient,
    private zone: NgZone,
    private notifications: NotificationStore
  ) {
    // Hydrate from storage on app start
    this.restoreFromStorage();

    // Multi-tab sync
    window.addEventListener('storage', (e) => {
      if (e.key === 'user' || e.key === 'tokenExpiresAt' || e.key === 'access_token') {
        this.restoreFromStorage(true);
      }
    });
  }

  // ----------------- AUTH FLOWS -----------------

  signUp(userFormData: string): Observable<UserModel> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<UserModel>(`${this.apiUrl}auth/signup`, userFormData, {
      headers,
      withCredentials: true,
      observe: 'response'
    }).pipe(
      tap((response) => this.consumeAuthResponse(response)),
      map((response) => response.body as UserModel)
    );
  }

  signIn(userFormData: any): Observable<HttpResponse<UserModel>> {
    return this.http.post<UserModel>(`${this.apiUrl}auth/signin`, userFormData, {
      withCredentials: true,
      observe: 'response'
    }).pipe(
      tap((response) => this.consumeAuthResponse(response))
    );
  }

  /** Logout locally + tell server */
  userLogout(): void {
    this.clearLogoutTimer();
    localStorage.removeItem('user');
    localStorage.removeItem('access_token');
    localStorage.removeItem('tokenExpiresAt');

    this._user$.next(null);
    this._isLoggedIn$.next(false);
    this.notifications.bumpUnreadCount?.();

    // Inform server (fire-and-forget)
    this.http.post(`${this.apiUrl}auth/logout`, {}, { withCredentials: true }).subscribe({
      error: () => {} // no-op
    });
  }

  // ----------------- PASSWORD -----------------

  requestPasswordReset(email: object): Observable<any> {
    return this.http.post(`${this.apiUrl}forgot-password`, email, {
      headers: { 'Content-Type': 'application/json' },
      responseType: 'text' as 'json'
    });
  }

  resetPassword(token: string, newPassword: string) {
    return this.http.post(`${this.apiUrl}reset-password`, { token, newPassword });
  }

  // ----------------- ACCESS HELPERS -----------------

  /** Backwards-compatible sync check, now using reactive snapshot + expiry */
  isAuthenticated(): boolean {
    // If we have state, trust it:
    if (this._isLoggedIn$.value && this._user$.value) return true;

    // Fallback: re-derive from storage (no server call)
    return this.restoreFromStorage(true);
  }

  /** Synchronous user getter (legacy) */
  getUser(): UserModel {
    const u = this._user$.value ?? this.safeParse<UserModel>(localStorage.getItem('user'));
    return u ?? { username: '', name: '', email: '', imageUrl: '', role: '', votedForRaces: [], commentedForRaces: [] };
  }

  storeUserModel(userModel: UserModel | null) {
    localStorage.setItem('user', JSON.stringify(userModel));
    this._user$.next(userModel);
  }

  getCookies(param: { accessToken: string; refreshToken: string | null }): Observable<HttpResponse<UserModel>> {
    return this.http.post<UserModel>(`${this.apiUrl}auth/cookies`, param, {
      withCredentials: true,
      observe: 'response'
    });
  }

  storeAccessTokenExpiration(expiresAt: string) {
    localStorage.setItem('tokenExpiresAt', expiresAt);
    const exp = this.parseExpiryToEpochSeconds(expiresAt);
    this.scheduleAutoLogout(exp);
  }

  // ----------------- AUTHZ HELPERS -----------------

  canEditRace(user: UserModel, raceId: string | null) {
    if (raceId === null) return false;
    const raceIdnum = Number(raceId);
    if (user.id === raceIdnum) return true;
    return user.role === 'ADMIN';
  }

  isAdmin(): boolean {
    const user = this.getUser();
    return !!user && user.role === 'ADMIN';
  }

  getGoogleAuthUrl() {
    const { oauth2AuthorizationBaseUrl, redirectUri } = environment;
    return `${oauth2AuthorizationBaseUrl}?redirect_uri=${encodeURIComponent(redirectUri)}`;
  }

  addRaceToVoted(raceId: number) {
    const user = this.getUser();
    user.votedForRaces = user.votedForRaces ?? [];
    if (!user.votedForRaces.includes(raceId)) {
      user.votedForRaces.push(raceId);
      this.storeUserModel(user);
    }
  }

  addRaceToCommented(raceId: number) {
    const user = this.getUser();
    user.commentedForRaces = user.commentedForRaces ?? [];
    if (!user.commentedForRaces.includes(raceId)) {
      user.commentedForRaces.push(raceId);
      this.storeUserModel(user);
    }
  }

  // ----------------- INTERNALS -----------------

  /** Centralizes how we handle auth responses that include headers + body */
  private consumeAuthResponse(response: HttpResponse<UserModel>) {
    const expiresAt = response.headers.get('Access-Token-Expires-At');
    const user = response.body ?? null;

    if (expiresAt) {
      localStorage.setItem('tokenExpiresAt', expiresAt);
      const expSec = this.parseExpiryToEpochSeconds(expiresAt);
      this.scheduleAutoLogout(expSec);
    }

    if (user) {
      this.storeUserModel(user);
      this._isLoggedIn$.next(true);
      // Trigger (re)loading of unread notifications if you want
      this.notifications.refresh?.();
      this.notifications.bumpUnreadCount?.();
    }
  }

  /**
   * Restores state from localStorage.
   * @returns true if valid & logged in; false otherwise.
   */
  private restoreFromStorage(silent = false): boolean {
    const storedUserStr = localStorage.getItem('user');
    const tokenExpStr = localStorage.getItem('tokenExpiresAt');

    if (!storedUserStr || !tokenExpStr) {
      if (!silent) this.userLogout();
      return false;
    }

    const expSec = this.parseExpiryToEpochSeconds(tokenExpStr);
    const nowSec = Math.floor(Date.now() / 1000);
    const skew = 15; // seconds

    if (!expSec || nowSec + skew >= expSec) {
      if (!silent) this.userLogout();
      return false;
    }

    const user = this.safeParse<UserModel>(storedUserStr);
    if (!user) {
      if (!silent) this.userLogout();
      return false;
    }

    this._user$.next(user);
    this._isLoggedIn$.next(true);
    this.scheduleAutoLogout(expSec);
    return true;
  }

  /** Parse an expiry value to epoch seconds; supports ISO string or epoch (sec/ms). */
  private parseExpiryToEpochSeconds(raw: string): number | null {
    if (!raw) return null;
    // Numeric?
    const num = Number(raw);
    if (!Number.isNaN(num) && Number.isFinite(num)) {
      // Heuristic: < 1e12 → seconds (10 digits), otherwise ms
      return num < 1e12 ? Math.floor(num) : Math.floor(num / 1000);
    }
    // ISO date?
    const ms = Date.parse(raw);
    if (!Number.isNaN(ms)) {
      return Math.floor(ms / 1000);
    }
    return null;
  }

  private scheduleAutoLogout(expSec: number | null) {
    this.clearLogoutTimer();
    if (!expSec) return;

    const nowMs = Date.now();
    const expMs = expSec * 1000;
    const skewMs = 10_000; // 10s skew
    const delay = Math.max(0, expMs - nowMs - skewMs);

    this.zone.runOutsideAngular(() => {
      this.logoutTimerId = setTimeout(() => {
        this.zone.run(() => this.userLogout());
      }, delay);
    });
  }

  private clearLogoutTimer() {
    if (this.logoutTimerId) {
      clearTimeout(this.logoutTimerId);
      this.logoutTimerId = null;
    }
  }

  private safeParse<T>(s: string | null): T | null {
    if (!s) return null;
    try { return JSON.parse(s) as T; } catch { return null; }
  }
}
