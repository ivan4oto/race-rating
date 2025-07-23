import {Component, Input, OnInit} from '@angular/core';
import {RaceComment} from "./comment/race-comment.model";
import {CommentComponent} from "./comment/comment.component";
import {NgForOf, NgIf} from "@angular/common";
import {CommentFormComponent} from "./comment-form/comment-form.component";
import {CommentService} from "./comment.service";
import {AuthService} from "../../auth/oauth2-redirect-handler/auth.service";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";

@Component({
  selector: 'app-comment-section',
  standalone: true,
  imports: [
    CommentComponent,
    NgForOf,
    CommentFormComponent,
    NgIf,
    MatButtonModule,
    MatIconModule,
    MatMenuModule
  ],
  templateUrl: './comment-section.component.html',
  styleUrl: './comment-section.component.scss'
})
export class CommentSectionComponent implements OnInit{
  @Input() raceId!: number;

  comments: RaceComment[] = [];
  sortedComments: RaceComment[] = [];

  currentSort: 'recent' | 'upvotes' | 'downvotes' = 'recent';
  selectedSortLabel = 'Most Recent';

  hasUserCommented!: boolean;

  constructor(private commentService: CommentService, private authService: AuthService) {
  }


  ngOnInit(): void {
    this.commentService.fetchCommentsByRaceId(this.raceId).subscribe(
      comments => {
        this.comments = comments;
        this.sortComments(this.currentSort);
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

  sortComments(criteria: 'recent' | 'upvotes' | 'downvotes') {
    this.currentSort = criteria;

    switch (criteria) {
      case 'recent':
        this.selectedSortLabel = 'Most Recent';
        this.sortedComments = [...this.comments].sort(
          (a, b) => b.createdAt - a.createdAt
        );
        break;
      case 'upvotes':
        this.selectedSortLabel = 'Upvotes';
        this.sortedComments = [...this.comments].sort(
          (a, b) => b.upvoteCount - a.upvoteCount
        );
        break;
      case 'downvotes':
        this.selectedSortLabel = 'Downvotes';
        this.sortedComments = [...this.comments].sort(
          (a, b) => b.downvoteCount - a.downvoteCount
        );
        break;
    }
  }


}
