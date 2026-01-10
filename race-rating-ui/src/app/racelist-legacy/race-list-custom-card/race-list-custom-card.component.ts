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

  getRatingColor(rating: number): string {
    if (rating >= 4.9) return '#104614';
    if (rating >= 4.6) return '#2e7931';
    if (rating >= 4.3) return '#689F38';
    if (rating >= 4.0) return '#689F38';
    if (rating >= 3.5) return '#F9A825';
    if (rating >= 3.0) return '#FB8C00';
    if (rating >= 2.5) return '#EF6C00';
    if (rating >= 2.0) return '#D84315';
    if (rating >= 1.5) return '#C62828';
    return '#B71C1C'
  }

  isRecentRace(date: Date | string): boolean {
    if (!date) return false;
    const now = new Date();
    const target = new Date(date);
    const diffMs = Math.abs(target.getTime() - now.getTime());
    const sevenDaysMs = 7 * 24 * 60 * 60 * 1000;
    return diffMs <= sevenDaysMs;
  }
}
