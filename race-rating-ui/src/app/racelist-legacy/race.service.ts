import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {RaceListModel, RaceSummaryDto} from "./race-list.model";
import {CreateRaceEventModel} from "../create-race/create-race-event.model";
import {RatingModel} from "../racedetail/rating-input/rating.model";

@Injectable({
  providedIn: 'root'
})
export class RaceService {
  private apiUrl = environment.apiUrl

  constructor(private http: HttpClient) {
  }

  fetchAllRaces(): Observable<RaceSummaryDto[]> {
    return this.http.get<RaceSummaryDto[]>(this.apiUrl + 'api/race/all')
  }

  fetchById(id: string): Observable<RaceListModel> {
    return this.http.get<RaceListModel>(this.apiUrl + `api/race/${id}`)
  }

  createRace(createRaceModel: CreateRaceEventModel): Observable<RaceListModel> {
    return this.http.post<RaceListModel>(
      this.apiUrl + `api/race`,
      createRaceModel,
      {withCredentials: true});
  }

  createRating(rating: RatingModel): Observable<RatingModel> {
    return this.http.post<RatingModel>(
      this.apiUrl + 'api/ratings',
      rating,
      {withCredentials: true}
    );
  }

  editRace(id: string, createRaceModel: CreateRaceEventModel): Observable<RaceListModel> {
    return this.http.put<RaceListModel>(
      this.apiUrl + `api/race/${id}`,
      createRaceModel,
      {withCredentials: true}
    );
  }

  uploadRaceLogo(id: string, file: File): Observable<RaceListModel> {
    const formData = new FormData();
    formData.append('logo', file);
    return this.http.put<RaceListModel>(
      this.apiUrl + `api/race/${id}/logo`,
      formData,
      {withCredentials: true}
    );
  }

  deleteRace(id: string): Observable<any> {
    return this.http.delete(this.apiUrl + `api/race/${id}`, {withCredentials: true});
  }
}
