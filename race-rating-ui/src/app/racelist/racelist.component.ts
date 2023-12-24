import {Component, OnInit} from '@angular/core';
import {Movie, RaceService} from "./race.service";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-racelist',
  standalone: true,
  imports: [
    NgForOf
  ],
  templateUrl: './racelist.component.html',
  styleUrl: './racelist.component.scss'
})
export class RacelistComponent implements OnInit{
  movies: Movie[] = [];
  constructor(
    private raceService: RaceService
  ) {
  }
  ngOnInit() {
    this.raceService.fetchData().subscribe({
      next: (data) => this.movies = data
    })
  }
}
