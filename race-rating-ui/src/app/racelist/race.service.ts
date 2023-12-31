import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {RaceListModel} from "./race-list.model";

@Injectable({
  providedIn: 'root'
})
export class RaceService {
  private apiUrl = environment.apiUrl

  constructor(private http: HttpClient) { }

  fetchAllRaces(): Observable<RaceListModel[]> {
    return this.http.get<RaceListModel[]>(this.apiUrl + 'public/races/all')
  }

  fetchById(id: string): Observable<RaceListModel> {
    return this.http.get<RaceListModel>(this.apiUrl + `public/race/${id}`)
  }
}
