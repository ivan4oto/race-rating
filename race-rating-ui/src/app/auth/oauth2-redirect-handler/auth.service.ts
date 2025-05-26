import {Injectable} from '@angular/core';
import {UserModel} from "./stored-user.model";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {map, Observable, tap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl

  constructor(private http: HttpClient) { }

  signUp(userFormData: string): Observable<UserModel> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
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
        const expiresAt = response.headers.get('Access-Token-Expires-At');
        if (expiresAt) {
          localStorage.setItem('tokenExpiresAt', expiresAt);
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
        const expiresAt = response.headers.get('Access-Token-Expires-At');
        if (expiresAt) {
          localStorage.setItem('tokenExpiresAt', expiresAt);
        }
        if (response.body) {
          this.storeUserModel(response.body);
        }
      })
    );
  }


  isAuthenticated() {
    const storedUserString = localStorage.getItem('user');
    const tokenExpString = localStorage.getItem('tokenExpiresAt');

    if (!storedUserString || !tokenExpString) {
      return false;
    }

    const tokenExp = parseInt(tokenExpString, 10);
    if (Date.now() / 1000 > tokenExp) {
      this.userLogout();
      return false;
    }
    return true;
  }



  userLogout() {
    localStorage.removeItem('user')
    localStorage.removeItem('access_token')
    localStorage.removeItem('tokenExpiresAt')
    this.http.post(this.apiUrl + 'auth/logout', {}, { withCredentials: true }).subscribe();
  }

  getUser(): UserModel {
    let storedUserString = localStorage.getItem('user');
    if (!storedUserString) {
      return { username: '', name: '', email: '', imageUrl: '', role: '', votedForRaces: [], commentedForRaces: [] }
    }
    return JSON.parse(storedUserString) as UserModel;
  }

  storeUserModel(userModel: UserModel) {
    localStorage.setItem('user', JSON.stringify(userModel));
  }
  public storeUserInformation() {
    return this.http.get<UserModel>(this.apiUrl + 'api/users/me', { withCredentials: true, observe: 'response' }).pipe(
      tap(response => {
        const expiresAt = response.headers.get('Access-Token-Expires-At');
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
    const { oauth2AuthorizationBaseUrl, redirectUri } = environment;
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
