import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {DatePipe, DecimalPipe, NgForOf, NgIf} from '@angular/common';
import {EMPTY, switchMap} from 'rxjs';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatDialog} from '@angular/material/dialog';
import {ToastrService} from 'ngx-toastr';
import {RaceService} from '../racelist-legacy/race.service';
import {RaceListModel} from '../racelist-legacy/race-list.model';
import {AuthService} from '../auth/oauth2-redirect-handler/auth.service';
import {ConfirmDialogComponent} from '../confirm-dialog/confirm-dialog.component';
import {TOASTR_ERROR_HEADER, TOASTR_SUCCESS_HEADER} from '../constants';
import {CommentService} from './comment-section/comment.service';
import {RaceComment, VoteResultDto} from './comment-section/comment/race-comment.model';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-racedetail',
  standalone: true,
  imports: [
    RouterLink,
    NgIf,
    NgForOf,
    FormsModule,
    DecimalPipe,
    DatePipe,
    MatIconModule,
    MatButtonModule,
    MatProgressBarModule
  ],
  templateUrl: './racedetail.component.html',
  styleUrl: './racedetail.component.scss'
})
export class RacedetailComponent implements OnInit{
  raceId: number | null = null;
  race: RaceListModel | null = null;
  hasUserVoted = false;
  hasUserCommented = false;
  isLoading = true;
  isSubmittingRating = false;
  isSubmittingComment = false;
  commentText = '';
  comments: RaceComment[] = [];

  ratingOptions = [1, 2, 3, 4, 5];
  ratingCategories = [
    {key: 'traceScore', label: 'Терен'},
    {key: 'vibeScore', label: 'Атмосфера'},
    {key: 'organizationScore', label: 'Организация'},
    {key: 'locationScore', label: 'Локация'},
    {key: 'valueScore', label: 'Цена/Такса'}
  ] as const;

  ratingValues: Record<string, number> = {
    traceScore: 0,
    vibeScore: 0,
    organizationScore: 0,
    locationScore: 0,
    valueScore: 0
  };

  readonly dialog = inject(MatDialog);

  constructor(
    private route: ActivatedRoute,
    private raceService: RaceService,
    private authService: AuthService,
    private commentService: CommentService,
    private toastr: ToastrService
    ) {

  }

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        if (!id) {
          return EMPTY;
        }
        this.raceId = Number(id);
        this.isLoading = true;
        this.updateUserVoteState();
        this.updateUserCommentState();
        return this.raceService.fetchById(id);
      })
    ).subscribe(
      {
        next: value => {
          this.race = value;
          if (this.raceId !== null) {
            this.loadComments(this.raceId);
          }
        },
        error: err => {
          console.log(err);
          this.isLoading = false;
        },
        complete: () => {
          this.isLoading = false;
        }
      }
    );
  }


  getRaceLogoUrl(raceId: number): string {
    return `${environment.s3BaseUrl}/race-logos/${raceId}/logo.png`;
  }

  get isAdmin() {
    return this.authService.isAdmin();
  }

  isUserAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  getRatingLabel(rating: number): string {
    if (rating < 1) return 'N/A';
    if (rating > 4.5) return 'Exceptional';
    if (rating > 4.25) return 'Superb';
    if (rating > 4) return 'Good';
    if (rating > 3) return 'Average';
    if (rating > 2) return 'Below Average';
    return 'Below Average';
  }

  getRatingPercent(value: number): string {
    const safeValue = Math.max(0, Math.min(5, value));
    return `${(safeValue / 5) * 100}%`;
  }

  setRating(key: string, value: number) {
    this.ratingValues[key] = value;
  }

  submitRating() {
    if (!this.raceId) {
      return;
    }
    const values = Object.values(this.ratingValues);
    if (values.some(value => value === 0)) {
      this.toastr.error('Please select a score for each category.', TOASTR_ERROR_HEADER);
      return;
    }
    this.isSubmittingRating = true;
    this.raceService.createRating({
      raceId: this.raceId,
      traceScore: this.ratingValues['traceScore'],
      vibeScore: this.ratingValues['vibeScore'],
      organizationScore: this.ratingValues['organizationScore'],
      locationScore: this.ratingValues['locationScore'],
      valueScore: this.ratingValues['valueScore']
    }).subscribe({
      next: () => {
        this.authService.addRaceToVoted(this.raceId!);
        this.hasUserVoted = true;
        this.refreshRace();
        this.toastr.success('Rating submitted.', TOASTR_SUCCESS_HEADER);
      },
      error: () => {
        this.toastr.error('Error while saving rating.', TOASTR_ERROR_HEADER);
      },
      complete: () => {
        this.isSubmittingRating = false;
      }
    });
  }

  submitComment() {
    if (!this.raceId || !this.commentText.trim()) {
      return;
    }
    this.isSubmittingComment = true;
    this.commentService.sendComment(this.raceId, this.commentText.trim()).subscribe({
      next: (comment) => {
        this.comments = [comment, ...this.comments];
        this.commentText = '';
        this.hasUserCommented = true;
        this.authService.addRaceToCommented(this.raceId!);
        this.toastr.success('Comment added.', TOASTR_SUCCESS_HEADER);
      },
      error: () => {
        this.toastr.error('Error while saving comment.', TOASTR_ERROR_HEADER);
      },
      complete: () => {
        this.isSubmittingComment = false;
      }
    });
  }

  voteOnComment(comment: RaceComment, isUpvote: boolean) {
    if (!this.isUserAuthenticated()) {
      this.toastr.error('You need to be logged in to vote.', TOASTR_ERROR_HEADER);
      return;
    }
    this.commentService.voteForComment(comment.id, isUpvote).subscribe({
      next: (response: VoteResultDto) => {
        if (!response.voteRegistered) {
          return;
        }
        if (response.currentVote === true) {
          comment.userVote = 'upvote';
          comment.upvoteCount++;
          comment.downvoteCount = comment.downvoteCount > 0 ? comment.downvoteCount - 1 : 0;
        } else if (response.currentVote === false) {
          comment.userVote = 'downvote';
          comment.downvoteCount++;
          comment.upvoteCount = comment.upvoteCount > 0 ? comment.upvoteCount - 1 : 0;
        }
      },
      error: () => {
        this.toastr.error('Error while voting.', TOASTR_ERROR_HEADER);
      }
    });
  }

  deleteRace() {
    if (!this.raceId) {
      return;
    }
    this.raceService.deleteRace(String(this.raceId)).subscribe(
      {
        next: () => {
          this.toastr.success('Race successfully deleted!', TOASTR_SUCCESS_HEADER);
        },
        error: err => {
          this.toastr.error('Error deleting race!', TOASTR_ERROR_HEADER);
        }
      }
    );
  }
  openDialog(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {isConfirmed: false, text: 'Are you sure you want to delete this race?'},
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      if (result !== undefined) {
        if (result) {
          this.deleteRace();
        }
        else {
          console.log('User cancelled dialog');
        }
      }
    });
  }

  getAvatarUrl(comment: RaceComment): string {
    if (comment.authorImageUrl && comment.authorImageUrl.trim().length > 0) {
      return comment.authorImageUrl;
    }
    return 'assets/home-icon.png';
  }

  private loadComments(raceId: number) {
    this.commentService.fetchCommentsByRaceId(raceId).subscribe({
      next: (comments) => {
        this.comments = [...comments].sort((a, b) => b.createdAt - a.createdAt);
        if (this.isUserAuthenticated() && comments.length > 0) {
          const commentIds = comments.map(comment => comment.id);
          this.commentService.getVotesForComments(commentIds).subscribe((votes) => {
            const voteMap = new Map<number, 'upvote' | 'downvote'>();
            votes.forEach(vote => {
              voteMap.set(vote.commentId, vote.isUpvote ? 'upvote' : 'downvote');
            });
            this.comments = this.comments.map(comment => ({
              ...comment,
              userVote: voteMap.get(comment.id)
            }));
          });
        }
      },
      error: () => {
        this.comments = [];
      }
    });
  }

  private refreshRace() {
    if (this.raceId === null) {
      return;
    }
    this.raceService.fetchById(String(this.raceId)).subscribe({
      next: (race) => {
        this.race = race;
      }
    });
  }

  private updateUserVoteState() {
    if (!this.isUserAuthenticated() || this.raceId === null) {
      this.hasUserVoted = false;
      return;
    }
    const user = this.authService.getUser();
    this.hasUserVoted = user.votedForRaces?.includes(this.raceId) ?? false;
  }

  private updateUserCommentState() {
    if (!this.isUserAuthenticated() || this.raceId === null) {
      this.hasUserCommented = false;
      return;
    }
    this.commentService.hasUserCommented$(this.raceId).subscribe({
      next: (hasCommented) => {
        this.hasUserCommented = hasCommented;
      },
      error: () => {
        this.hasUserCommented = false;
      }
    });
  }
}
