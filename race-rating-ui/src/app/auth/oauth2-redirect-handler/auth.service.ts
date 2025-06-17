import {Injectable} from '@angular/core';
import {UserModel} from "./stored-user.model";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {map, Observable, Subscription, tap, timer} from "rxjs";
import {TOKEN_EXPIRES_AT_HEADER} from "../../constants";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl
  private refreshSubscription?: Subscription;
  private refreshBufferMs = 60 * 500;

  constructor(
    private http: HttpClient) {
  }

  signUp(userFormData: string): Observable<UserModel> {
    const headers = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post<UserModel>(
      this.apiUrl + 'auth/signup',
      userFormData,
      {
        headers: headers,
        withCredentials: true,
        observe: 'response'
      }
    ).pipe(
      tap(response => {
        const expiresAt = response.headers.get(TOKEN_EXPIRES_AT_HEADER);
        if (expiresAt) {
          localStorage.setItem('tokenExpiresAt', expiresAt);
          this.scheduleRefresh(expiresAt);
        }
        if (response.body) {
          this.storeUserModel(response.body);
        }
      }),
      map(response => response.body as UserModel)
    );
  }


  signIn(userFormData: any) {
    return this.http.post<UserModel>(
      this.apiUrl + 'auth/signin',
      userFormData,
      {
        withCredentials: true,
        observe: 'response' // This allows us to access the response headers
      }
    ).pipe(
      tap(response => {
        const expiresAt = response.headers.get(TOKEN_EXPIRES_AT_HEADER);
        if (expiresAt) {
          localStorage.setItem('tokenExpiresAt', expiresAt);
          this.scheduleRefresh(expiresAt);
        }
        if (response.body) {
          this.storeUserModel(response.body);
        }
      })
    );
  }

  refreshToken() {
    return this.http.post<{ accessToken: string; refreshToken: string }>(
      this.apiUrl + 'auth/refresh-token',
      {}, // no body
      {observe: 'response', withCredentials: true}).pipe(
      tap(response => {
        const expiresAt = response.headers.get(TOKEN_EXPIRES_AT_HEADER);
        if (expiresAt) {
          console.log('Expires at: ', expiresAt);
          localStorage.setItem('tokenExpiresAt', expiresAt);
          this.scheduleRefresh(expiresAt);
        }
      })
    ).subscribe({
      next: () => {
        // New expiry time will be picked up by the interceptor
        console.log('Token refreshed');
      },
      error: (err) => {
        console.error('Token refresh failed', err);
        if (this.refreshSubscription) {
          this.refreshSubscription.unsubscribe();
        }
      }
    });
  }

  private scheduleRefresh(expiresAt: string) {
    const expiresAtDate = new Date(Number(expiresAt) * 1000);
    const now = new Date().getTime();
    const expiry = expiresAtDate.getTime();
    const delay = Math.max(expiry - now - this.refreshBufferMs, 0);
    this.refreshSubscription = timer(delay).subscribe(() => {
      this.refreshToken();
    });
  }

  isAuthenticated() {
    const storedUserString = localStorage.getItem('user');
    const tokenExpString = localStorage.getItem('tokenExpiresAt');

    if (!storedUserString || !tokenExpString) {
      return false;
    }

    const tokenExp = parseInt(tokenExpString, 10);
    if (Date.now() / 1000 > tokenExp) {
      console.log('Logging user out!')
      this.userLogout();
      return false;
    }
    return true;
  }

  userLogout() {
    localStorage.removeItem('user')
    localStorage.removeItem('access_token')
    localStorage.removeItem('tokenExpiresAt')
    this.http.post(this.apiUrl + 'auth/logout', {}, {withCredentials: true}).subscribe();
  }

  getUser(): UserModel {
    let storedUserString = localStorage.getItem('user');
    if (!storedUserString) {
      return {username: '', name: '', email: '', imageUrl: '', role: '', votedForRaces: [], commentedForRaces: []}
    }
    return JSON.parse(storedUserString) as UserModel;
  }

  storeUserModel(userModel: UserModel) {
    localStorage.setItem('user', JSON.stringify(userModel));
  }

  public storeUserInformation() {
    return this.http.get<UserModel>(this.apiUrl + 'api/users/me', {withCredentials: true, observe: 'response'}).pipe(
      tap(response => {
        const expiresAt = response.headers.get(TOKEN_EXPIRES_AT_HEADER);
        if (expiresAt) {
          localStorage.setItem('tokenExpiresAt', expiresAt);
        }
        if (response.body) {
          this.storeUserModel(response.body);
        }
      })
    );
  }

  canEditRace(user: UserModel, raceId: string | null) {
    if (raceId === null) {
      return false;
    }
    const raceIdnum = Number(raceId);
    if (user.id === raceIdnum) {
      return true;
    }
    return user.role === 'ADMIN';
  }

  getGoogleAuthUrl() {
    const {oauth2AuthorizationBaseUrl, redirectUri} = environment;
    return `${oauth2AuthorizationBaseUrl}?redirect_uri=${redirectUri}`;
  }

  addRaceToVoted(raceId: number) {
    const user = this.getUser();
    user.votedForRaces.push(raceId);
    this.storeUserModel(user);
  }

  addRaceToCommented(raceId: number) {
    const user = this.getUser();
    user.commentedForRaces.push(raceId);
    this.storeUserModel(user);
  }
}
