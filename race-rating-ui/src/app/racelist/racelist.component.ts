import {Component, OnInit} from '@angular/core';
import {RaceService} from "./race.service";
import {NgForOf} from "@angular/common";
import {RacelistCardComponent} from "./racelist-card/racelist-card.component";
import {RaceListModel} from "./race-list.model";
import {SearchBarComponent} from "./search-bar/search-bar.component";
import {MatSelectModule} from "@angular/material/select";
import {MatDividerModule} from "@angular/material/divider";
import {MatSliderModule} from "@angular/material/slider";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {ThemePalette} from "@angular/material/core";
import {FormsModule} from "@angular/forms";

export interface Terrain {
  name: string;
  completed: boolean;
  color: ThemePalette;
  subtasks?: Terrain[];
}
@Component({
  selector: 'app-racelist',
  standalone: true,
  imports: [
    NgForOf,
    RacelistCardComponent,
    SearchBarComponent,
    MatSelectModule,
    MatDividerModule,
    MatSliderModule,
    MatCheckboxModule,
    FormsModule
  ],
  templateUrl: './racelist.component.html',
  styleUrl: './racelist.component.scss'
})
export class RacelistComponent implements OnInit{

  minDistance: number = 0;
  maxDistance: number = 200;
  minElevation: number = 0;
  maxElevation: number = 5000;
  selectedTerrain: string = 'all';

  roadTerrain: Terrain = {
    name: 'Road',
    completed: false,
    color: 'primary'
  }

  offRoadTerrain: Terrain = {
    name: 'Off-road',
    completed: false,
    color: 'primary',
    subtasks: [
      {name: 'Trail', completed: false, color: 'primary'},
      {name: 'Technical trail', completed: false, color: 'primary'},
      {name: 'Big mountain', completed: false, color: 'accent'},
    ],
  };
  allRaces: RaceListModel[] = [];
  // allRaces: RaceListModel[] = [
  //   {id: 1, name: 'Balkan Ultra',  distance: 78, elevation: 6700, rating: 5, logoUrl: 'https://balkanultra.s3.eu-central-1.amazonaws.com/images/home/why.jpg'},
  //   {id: 2, name: 'Tryavna Ultra',  distance: 150, elevation: 6684, rating: 4, logoUrl: 'https://i.ytimg.com/vi/BgqGre6bkBg/sddefault.jpg'},
  //   {id: 3, name: 'Vitosha 100',  distance: 100, elevation: 1600, rating: 5, logoUrl: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTwe3KBFgp51Is6hpwYzt4LvW13jJIQxIHblTkdL5GiaVWBqFFg4zJCTDjqKNRsQvskQOI&usqp=CAU'},
  //   {id: 4, name: 'Thracian Ultra',  distance: 50, elevation: 2630, rating: 4, logoUrl: 'https://thracian-ultra.com/wordpress/wp-content/uploads/2023/11/TU-Logo.png'},
  //   {id: 5, name: 'Gela Run',  distance: 21, elevation: 1060, rating: 5, logoUrl: 'https://thracian-ultra.com/wordpress/wp-content/uploads/2023/11/TU-Logo.png'},
  //   {id: 6, name: 'Pancharevo Trail',  distance: 42, elevation: 1882, rating: 3, logoUrl: 'https://pancharevo.begach.com/wp-content/uploads/2022/07/Pancharevo-Trail-marathon-Logo-Square.png'},
  //   {id: 7, name: 'Kodzha Kaya',  distance: 42, elevation: 2500, rating: 5, logoUrl: 'https://www.race-tracking.com/i/fb/67.jpg'}
  // ];

  constructor(
    private raceService: RaceService
  ) {
  }
  ngOnInit() {
    this.raceService.fetchAllRaces().subscribe({
      next: (data: RaceListModel[]) => this.allRaces = data
    })
  }


  allComplete: boolean = false;

  updateAllComplete() {
    this.allComplete = this.offRoadTerrain.subtasks != null && this.offRoadTerrain.subtasks.every(t => t.completed);
  }

  someComplete(): boolean {
    if (this.offRoadTerrain.subtasks == null) {
      return false;
    }
    return this.offRoadTerrain.subtasks.filter(t => t.completed).length > 0 && !this.allComplete;
  }

  setAll(completed: boolean) {
    this.allComplete = completed;
    if (this.offRoadTerrain.subtasks == null) {
      return;
    }
    this.offRoadTerrain.subtasks.forEach(t => (t.completed = completed));
  }
}
