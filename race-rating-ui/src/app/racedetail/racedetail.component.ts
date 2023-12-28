import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {EMPTY, switchMap} from "rxjs";
import {RaceService} from "../racelist/race.service";
import {RaceListModel} from "../racelist/RaceListModel";
import {RatingDisplayComponent} from "../racelist/rating-display/rating-display.component";

@Component({
  selector: 'app-racedetail',
  standalone: true,
  imports: [
    RatingDisplayComponent
  ],
  templateUrl: './racedetail.component.html',
  styleUrl: './racedetail.component.scss'
})
export class RacedetailComponent implements OnInit{
  id: string | null = null;
  race!: RaceListModel;
  constructor(
    private route: ActivatedRoute,
    private raceService: RaceService
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
  }

}
