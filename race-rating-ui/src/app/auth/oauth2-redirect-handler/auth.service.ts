import { Injectable } from '@angular/core';
import {parseJwt} from "../../helpers";
import {StoredUserModel, StoredUserModelData, UserModel} from "./stored-user.model";
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
    const authenticatedUser = { data, token }
    localStorage.setItem('user', JSON.stringify(authenticatedUser))
    localStorage.setItem('access_token', token);
  }

  isAuthenticated() {
    let storedUserString = localStorage.getItem('user')
    if (storedUserString === null) {
      return false
    }
    const storedUser: StoredUserModel = JSON.parse(storedUserString) as StoredUserModel;
    if (Date.now() > storedUser.data.exp * 1000) {
      console.log('Loggin out');
      console.log(Date.now());
      console.log(storedUser.data.exp * 1000);
      this.userLogout()
      return false
    }
    return true
  }

  userLogout() {
    localStorage.removeItem('user')
  }

  getUser(): StoredUserModelData {
    let storedUserString = localStorage.getItem('user');
    if (!storedUserString) {
      return {email: "", exp: 0, name: "", rol: [], avatarUrl: ''}
    }
    const user: StoredUserModel = JSON.parse(storedUserString) as StoredUserModel
    return user.data;
  }

  fetchUserData(): Observable<UserModel> {
    return this.http.get<UserModel>(this.apiUrl + 'api/users/me')
  }
  storeUserInformation() {
    this.fetchUserData().subscribe({
      next: userModel => {
        console.log(userModel);
        localStorage.setItem('loggedUser', JSON.stringify(userModel))
      },
      error: err => console.log(err)
    });
  }
}
