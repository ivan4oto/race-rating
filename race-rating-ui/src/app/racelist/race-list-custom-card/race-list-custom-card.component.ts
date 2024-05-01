import {Component, Input} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {RaceListModel} from "../race-list.model";
import {DatePipe} from "@angular/common";
import {RatingDisplayComponent} from "../rating-display/rating-display.component";

@Component({
  selector: 'app-race-list-custom-card',
  standalone: true,
  imports: [
    MatCardModule,
    DatePipe,
    RatingDisplayComponent
  ],
  templateUrl: './race-list-custom-card.component.html',
  styleUrl: './race-list-custom-card.component.scss'
})
export class RaceListCustomCardComponent {
  @Input() data!: RaceListModel
}
