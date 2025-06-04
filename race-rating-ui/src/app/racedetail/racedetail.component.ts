import {Component, OnInit} from '@angular/core';
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
import {RATINGS_CYRILIC} from "../constants";
import {AvgRatingWidgetComponent} from "./avg-rating-widget/avg-rating-widget.component";
import {RatingBarComponent} from "./rating-bar/rating-bar.component";
import {UserModel} from "../auth/oauth2-redirect-handler/stored-user.model";

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
    DatePipe,
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
  constructor(
    private route: ActivatedRoute,
    private raceService: RaceService,
    private authService: AuthService
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
  protected readonly RATINGS_CYRILIC = RATINGS_CYRILIC;
}
