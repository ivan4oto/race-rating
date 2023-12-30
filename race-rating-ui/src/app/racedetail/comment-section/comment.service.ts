import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {RaceComment} from "./comment/race-comment.model";

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = environment.apiUrl

  constructor(private http: HttpClient) { }
  fetchCommentsByRaceId(raceId: number): Observable<RaceComment[]> {
    return this.http.get<RaceComment[]>(this.apiUrl + `public/comments/race/${raceId}`)
  }
}
