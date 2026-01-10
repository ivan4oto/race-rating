import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgForOf} from "@angular/common";
import {MatButtonModule} from "@angular/material/button";
import {RaceService} from "../../racelist-legacy/race.service";
import {ActivatedRoute} from "@angular/router";
import {AuthService} from "../../auth/oauth2-redirect-handler/auth.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-rating-input',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    MatButtonModule
  ],
  templateUrl: './rating-input.component.html',
  styleUrl: './rating-input.component.scss'
})
export class RatingInputComponent implements OnInit{
  raceId!: number;
  ratingLabels: string[] = ['Зле', 'Слабо', 'Средно', 'Добре', 'Отлично!']
  labels: string[] = ['Атмосфера', 'Такса', 'Трасе', 'Локация', 'Организация'];
  ratings: number[] = [0, 0, 0, 0, 0];
  constructor(
    private raceService: RaceService,
    private route: ActivatedRoute,
    private authService: AuthService,
    private toastr: ToastrService,
  ) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.raceId = +params['id'];
    });
  }

  submitRatings() {
    console.log(this.ratings)
    if (this.ratings.some(rating => rating === 0)) {
      this.toastr.error("Please select all the ratings!", "Error!")
      return;
    }
    this.raceService.createRating({
      raceId: this.raceId,
      vibeScore: this.ratings[0],
      valueScore: this.ratings[1],
      traceScore: this.ratings[2],
      locationScore: this.ratings[3],
      organizationScore: this.ratings[4],
    }).subscribe(
      {
        next: rating => {
          this.authService.addRaceToVoted(this.raceId);
          this.toastr.success("Rating successfully added!", "Success!")
        },
        error: error => {
          this.toastr.error("Error while saving rating!", "Error!")
        }
      }
    )
  }

  setRating(index: number, value: number) {
    this.ratings[index] = value;
  }
  isSelected(index: number, value: number): boolean {
    return this.ratings[index] === value;
  }
}
