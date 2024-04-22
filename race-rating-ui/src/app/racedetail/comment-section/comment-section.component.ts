import {Component, Input, OnInit} from '@angular/core';
import {RaceComment} from "./comment/race-comment.model";
import {dummyComments} from "./dummyComments";
import {CommentComponent} from "./comment/comment.component";
import {NgForOf, NgIf} from "@angular/common";
import {CommentFormComponent} from "./comment-form/comment-form.component";
import {CommentService} from "./comment.service";
import {AuthService} from "../../auth/oauth2-redirect-handler/auth.service";

@Component({
  selector: 'app-comment-section',
  standalone: true,
  imports: [
    CommentComponent,
    NgForOf,
    CommentFormComponent,
    NgIf
  ],
  templateUrl: './comment-section.component.html',
  styleUrl: './comment-section.component.scss'
})
export class CommentSectionComponent implements OnInit{
  @Input() raceId!: number;
  comments: RaceComment[] = dummyComments;
  hasUserCommented!: boolean;

  constructor(private commentService: CommentService, private authService: AuthService) {
  }
  ngOnInit(): void {
    this.commentService.fetchCommentsByRaceId(this.raceId).subscribe(
      comments => {
        this.comments = comments;
      }
    )
    this.hasUserCommented = this.authService.getUser().commentedForRaces.includes(this.raceId);
  }

  onCommentAdded(comment: RaceComment) {
    this.comments.push(comment);
  }
  public isUserAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
}
