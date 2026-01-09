import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf, DecimalPipe} from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {RaceService} from '../racelist/race.service';
import {RaceSummaryDto} from '../racelist/race-list.model';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-racelistnew',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    DecimalPipe,
    MatIconModule
  ],
  templateUrl: './racelistnew.component.html',
  styleUrl: './racelistnew.component.scss'
})
export class RacelistnewComponent implements OnInit {
  races: RaceSummaryDto[] = [];
  isLoading = true;

  constructor(private raceService: RaceService) {
  }

  ngOnInit() {
    this.raceService.fetchAllRaces().subscribe({
      next: (data) => {
        this.races = data ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  getRaceLogoUrl(raceId: number): string {
    return `${environment.s3BaseUrl}/race-logos/${raceId}/logo.png`;
  }

  getStars(averageRating: number): {filled: boolean}[] {
    const fullStars = Math.max(0, Math.min(5, Math.round(averageRating)));
    return Array.from({length: 5}, (_, index) => ({filled: index < fullStars}));
  }

  getTotalVotes(race: RaceSummaryDto): number {
    return race.totalVotes ?? race.ratingsCount ?? 0;
  }
}
