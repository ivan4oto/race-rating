import {Component, Input} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {RaceSummaryDto} from "../race-list.model";
import {DatePipe, NgClass} from "@angular/common";
import {RatingDisplayComponent} from "../rating-display/rating-display.component";
import {MatIconModule} from "@angular/material/icon";
import {environment} from "../../../environments/environment";

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

  getRaceLogoUrl(raceId: number): string {
    return `${environment.s3BaseUrl}/race-logos/${raceId}/logo.png`;
  }
}
