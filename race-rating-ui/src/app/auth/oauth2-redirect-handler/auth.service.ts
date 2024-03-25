import {Injectable} from '@angular/core';
import {parseJwt} from "../../helpers";
import {UserModel} from "./stored-user.model";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl

  constructor(private http: HttpClient) { }
  handleLogin(token: string) {
    const data = parseJwt(token)
    localStorage.setItem('access_token', token);
    localStorage.setItem('tokenExpiresAt', data.exp);
    this.storeUserInformation();
  }

  isAuthenticated() {
    let storedUserString = localStorage.getItem('user')
    if (storedUserString === null) {
      return false
    }
    const tokenExpString = localStorage.getItem('tokenExpiresAt');
    if (tokenExpString === null) {
      return true;
    }
    const tokenExp = JSON.parse(tokenExpString);
    if (Date.now() > tokenExp * 1000) {
      console.log('Loggin out');
      console.log(Date.now());
      console.log(tokenExp * 1000);
      this.userLogout()
      return false
    }
    return true
  }

  userLogout() {
    localStorage.removeItem('user')
    localStorage.removeItem('access_token')
    localStorage.removeItem('tokenExpiresAt')
  }

  getUser(): UserModel {
    let storedUserString = localStorage.getItem('user');
    if (!storedUserString) {
      return { username: '', name: '', email: '', imageUrl: '', role: '', votedForRaces: [] }
    }
    return JSON.parse(storedUserString) as UserModel;
  }

  private fetchUserData(): Observable<UserModel> {
    return this.http.get<UserModel>(this.apiUrl + 'api/users/me')
  }
  private storeUserInformation() {
    this.fetchUserData().subscribe({
      next: userModel => {
        console.log(userModel);
        localStorage.setItem('user', JSON.stringify(userModel))
      },
      error: err => console.log(err)
    });
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
    return `${oauth2AuthorizationBaseUrl}?redirect_uri=${encodeURIComponent(redirectUri)}`;
  }
}
