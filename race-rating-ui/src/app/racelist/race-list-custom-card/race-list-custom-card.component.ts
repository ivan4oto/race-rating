import {Component, Input} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {RaceSummaryDto} from "../race-list.model";
import {DatePipe, NgClass} from "@angular/common";
import {RatingDisplayComponent} from "../rating-display/rating-display.component";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-race-list-custom-card',
  standalone: true,
  imports: [
    MatCardModule,
    DatePipe,
    RatingDisplayComponent,
    MatIconModule,
    NgClass
  ],
  templateUrl: './race-list-custom-card.component.html',
  styleUrl: './race-list-custom-card.component.scss'
})
export class RaceListCustomCardComponent {
  @Input() data!: RaceSummaryDto
}
