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
