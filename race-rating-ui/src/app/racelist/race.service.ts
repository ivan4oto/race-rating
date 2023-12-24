import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

export interface Movie {
  createdAt: string;
  imdb: string;
  poster: string;
  title: string;
}

@Injectable({
  providedIn: 'root'
})
export class RaceService {
  private apiUrl = environment.apiUrl

  constructor(private http: HttpClient) { }

  fetchData(): Observable<Movie[]> {
    return this.http.get<Movie[]>(this.apiUrl + 'api/movies');
  }
}
