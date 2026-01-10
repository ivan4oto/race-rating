import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {RaceListModel} from "../racelist-legacy/race-list.model";
import {UserModel} from "../auth/oauth2-redirect-handler/stored-user.model";

@Injectable({
  providedIn: 'root'
})
export class AdminPanelService {
  private apiUrl = environment.apiUrl
  constructor(private http: HttpClient) { }

  fetchAllUsers(): Observable<UserModel[]> {
    return this.http.get<UserModel[]>(this.apiUrl + 'admin/users', {withCredentials: true})
  }

  updateUser(userModel: UserModel): Observable<UserModel> {
    return this.http.put<UserModel>(this.apiUrl + 'admin/users/update', userModel, {withCredentials: true})
  }

}
