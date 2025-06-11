import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from "@angular/router";
import {EMPTY, switchMap} from "rxjs";
import {RaceService} from "../racelist/race.service";
import {RaceListModel} from "../racelist/race-list.model";
import {RatingDisplayComponent} from "../racelist/rating-display/rating-display.component";
import {CommentSectionComponent} from "./comment-section/comment-section.component";
import {RatingInputComponent} from "./rating-input/rating-input.component";
import {AuthService} from "../auth/oauth2-redirect-handler/auth.service";
import {DatePipe, NgIf} from "@angular/common";
import {CarouselComponent} from "../carousel/carousel.component";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatDividerModule} from "@angular/material/divider";
import {RATINGS_CYRILIC, TOASTR_ERROR_HEADER, TOASTR_SUCCESS_HEADER} from "../constants";
import {AvgRatingWidgetComponent} from "./avg-rating-widget/avg-rating-widget.component";
import {RatingBarComponent} from "./rating-bar/rating-bar.component";
import {UserModel} from "../auth/oauth2-redirect-handler/stored-user.model";
import {ConfirmDialogComponent} from "../confirm-dialog/confirm-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-racedetail',
  standalone: true,
  imports: [
    CommentSectionComponent,
    RatingInputComponent,
    NgIf,
    CarouselComponent,
    MatIconModule,
    RouterLink,
    MatButtonModule,
    MatDividerModule,
    AvgRatingWidgetComponent,
    RatingBarComponent
  ],
  templateUrl: './racedetail.component.html',
  styleUrl: './racedetail.component.scss'
})
export class RacedetailComponent implements OnInit{
  public isAdmin: boolean = false;
  id: string | null = null;
  hasUserVoted!: boolean;
  race!: RaceListModel;
  readonly dialog = inject(MatDialog);

  constructor(
    private route: ActivatedRoute,
    private raceService: RaceService,
    private authService: AuthService,
    private toastr: ToastrService,
    ) {

  }

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        this.id = params.get('id');
        return this.id ? this.raceService.fetchById(this.id) : EMPTY;
      })
    ).subscribe(
      {
        next: value => this.race = value,
        error: err => console.log(err)
      }
    )
    const user: UserModel = this.authService.getUser();
    this.isAdmin = user.role === 'ADMIN';
    this.hasUserVoted = user.votedForRaces.includes(Number(this.id));
  }

  public isUserAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  deleteRace() {
    this.raceService.deleteRace(this.id!).subscribe(
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


  protected readonly RATINGS_CYRILIC = RATINGS_CYRILIC;
}
