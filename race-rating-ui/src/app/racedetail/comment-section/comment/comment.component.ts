import {Component, Input} from '@angular/core';
import {RaceComment, VoteResultDto} from "./race-comment.model";
import {DatePipe, NgStyle} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {CommentService} from "../comment.service";
import {AuthService} from "../../../auth/oauth2-redirect-handler/auth.service";

@Component({
  selector: 'app-comment',
  standalone: true,
  imports: [
    DatePipe,
    MatIconModule,
    MatButtonModule,
    NgStyle
  ],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss'
})
export class CommentComponent {
  nonVotedIconColor: string = '#696969';
  votedIconColor: string = '#000000';


  constructor(private commentService: CommentService, private authService: AuthService) {
  }

  @Input() raceComment!: RaceComment

  vote(vote: boolean) {
    if (!this.authService.isAuthenticated()) {
      console.warn('Not authenticated');
      return;
    }
    this.commentService.voteForComment(this.raceComment.id, vote).subscribe(
      {
        next: (response: VoteResultDto) => {
          if (response.voteRegistered) {
            if (response.currentVote === true) {
              this.raceComment.upvoteCount++;
              this.raceComment.downvoteCount = this.raceComment.downvoteCount > 0 ? this.raceComment.downvoteCount - 1 : 0;
            } else {
              this.raceComment.downvoteCount++;
              this.raceComment.upvoteCount = this.raceComment.upvoteCount > 0 ? this.raceComment.upvoteCount - 1 : 0;
            }
          } else {
            console.warn('Vote not registered');
          }

        },
        error: (error) => {
          console.log(error)
        }
      }
    )
  }
}
