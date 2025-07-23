import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {RaceComment, VoteResultDto} from "./race-comment.model";
import {DatePipe, NgIf, NgStyle} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {CommentService} from "../comment.service";
import {AuthService} from "../../../auth/oauth2-redirect-handler/auth.service";
import {ToastrService} from "ngx-toastr";
import {TOASTR_ERROR_HEADER} from "../../../constants";

@Component({
  selector: 'app-comment',
  standalone: true,
  imports: [
    DatePipe,
    MatIconModule,
    MatButtonModule,
    NgStyle,
    NgIf
  ],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss'
})
export class CommentComponent implements OnChanges, OnInit {
  nonVotedIconColor: string = '#696969';
  avatarUrl: string = '';
  votedIconColor: string = '#000000';


  constructor(private commentService: CommentService, private authService: AuthService, private toastr: ToastrService) {
  }

  @Input() raceComment!: RaceComment

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['raceComment'] && this.raceComment) {
      if (this.raceComment.authorImageUrl?.trim()) {
        this.avatarUrl = this.raceComment.authorImageUrl;
      }
    }
  }

  deleteComment() {
    this.commentService.deleteComment(this.raceComment.raceId, this.raceComment.id).subscribe(
      {
        next() {
          console.log('Comment deleted');
        },
        error: (error) => {
          console.log(error)
        }
      }
    )
  }

  vote(vote: boolean) {
    if (!this.authService.isAuthenticated()) {
      this.toastr.error('You need to be logged in to vote!', TOASTR_ERROR_HEADER);
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

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }
}
