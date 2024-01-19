import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {EMPTY, switchMap} from "rxjs";
import {RaceService} from "../racelist/race.service";
import {RaceListModel} from "../racelist/race-list.model";
import {RatingDisplayComponent} from "../racelist/rating-display/rating-display.component";
import {CommentSectionComponent} from "./comment-section/comment-section.component";
import {RatingInputComponent} from "./rating-input/rating-input.component";
import {AuthService} from "../auth/oauth2-redirect-handler/auth.service";
import {NgIf} from "@angular/common";
import {CarouselComponent} from "../carousel/carousel.component";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-racedetail',
  standalone: true,
  imports: [
    RatingDisplayComponent,
    CommentSectionComponent,
    RatingInputComponent,
    NgIf,
    CarouselComponent,
    MatIconModule
  ],
  templateUrl: './racedetail.component.html',
  styleUrl: './racedetail.component.scss'
})
export class RacedetailComponent implements OnInit{
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
    this.hasUserVoted = this.authService.getUser().votedForRaces.includes(Number(this.id));
  }

  public isUserAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

}
