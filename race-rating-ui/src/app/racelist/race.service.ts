import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {RaceListModel} from "./race-list.model";
import {CreateRaceEventModel} from "../create-race/create-race-event.model";
import {RatingModel} from "../racedetail/rating-input/rating.model";

@Injectable({
  providedIn: 'root'
})
export class RaceService {
  private apiUrl = environment.apiUrl

  constructor(private http: HttpClient) { }

  fetchAllRaces(): Observable<RaceListModel[]> {
    return this.http.get<RaceListModel[]>(this.apiUrl + 'api/race/all')
  }
  fetchById(id: string): Observable<RaceListModel> {
    return this.http.get<RaceListModel>(this.apiUrl + `api/race/${id}`)
  }
  createRace(createRaceModel: CreateRaceEventModel): Observable<RaceListModel> {
    return this.http.post<RaceListModel>(this.apiUrl + `api/race`, createRaceModel);
  }
  createRating(rating: RatingModel): Observable<RatingModel> {
    return this.http.post<RatingModel>(this.apiUrl + 'api/ratings', rating);
  }
}
