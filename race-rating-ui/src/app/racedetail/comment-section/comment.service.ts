import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {CommentVoteStatus, RaceComment, VoteResultDto} from "./comment/race-comment.model";

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = environment.apiUrl

  constructor(private http: HttpClient) { }

  fetchCommentsByRaceId(raceId: number): Observable<RaceComment[]> {
    return this.http.get<RaceComment[]>(this.apiUrl + `api/comments/race/${raceId}`)
  }


  getVotesForComments(commentIds: number[]): Observable<CommentVoteStatus[]> {
    const params = new HttpParams()
      .set('commentIds', commentIds.join(','));

    return this.http.get<CommentVoteStatus[]>(this.apiUrl + `api/comment-votes`, { params, withCredentials: true });
  }


  sendComment(raceId: number, comment: string): Observable<RaceComment> {
    return this.http.post<RaceComment>(this.apiUrl + `api/comments/${raceId}`, {
      commentText: comment,
    }, { withCredentials: true });
  }

  voteForComment(commentId: number, isUpVote: boolean): Observable<VoteResultDto> {
    return this.http.post<VoteResultDto>(this.apiUrl + `api/comments/vote`, {
      isUpVote: isUpVote,
      commentId: commentId,
    }, { withCredentials: true })
  }

  deleteComment(raceId: number, commentId: number): Observable<any> {
    return this.http.delete(this.apiUrl + `api/comments/${raceId}/${commentId}`, { withCredentials: true })
  }

  hasUserCommented$(raceId: number) {
    return this.http.get<{ hasCommented: boolean }>(this.apiUrl + `api/races/${raceId}/comments/me`, { withCredentials: true })
      .pipe(map(r => r.hasCommented));
  }

}
