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
import Fuse from "fuse.js";
import {AdvancedSearchComponent, FilterData} from "./advanced-search/advanced-search.component";
import {MatDialog} from "@angular/material/dialog";
import {
  FILTER_MAXIMUM_DISTANCE,
  FILTER_MAXIMUM_ELEVATION,
  FILTER_MINIMAL_DISTANCE,
  FILTER_MINIMAL_ELEVATION, TERRAINS
} from "../constants";
import {MatPaginatorModule, PageEvent} from "@angular/material/paginator";

export interface Terrain {
  name: string;
  checked: boolean;
  color: ThemePalette;
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
    FormsModule,
    MatPaginatorModule
  ],
  templateUrl: './racelist.component.html',
  styleUrl: './racelist.component.scss'
})
export class RacelistComponent implements OnInit {
  allRaces: RaceListModel[] = [];
  filteredRaces: RaceListModel[] = [];
  currentRaces: RaceListModel[] = [];
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];
  fuse!: Fuse<RaceListModel>;
  fuseOptions = {
    keys: ["name"],
    includeScore: true,
  };

  filterData: FilterData = {
    terrains: TERRAINS,
    selectedMinElevation: FILTER_MINIMAL_ELEVATION,
    selectedMaxElevation: FILTER_MAXIMUM_ELEVATION,
    selectedMinDistance: FILTER_MINIMAL_DISTANCE,
    selectedMaxDistance: FILTER_MAXIMUM_DISTANCE
  }
  constructor(
    private raceService: RaceService,
    public dialog: MatDialog
  ) {
  }

  ngOnInit() {
    this.raceService.fetchAllRaces().subscribe({
      next: (data: RaceListModel[]) => {
        this.allRaces = data;
        this.filteredRaces = this.allRaces;
        this.updateCurrentRaces(0);
        this.fuse = new Fuse(this.allRaces, this.fuseOptions);
      }
    })
  }


  getSelectedTerrainNames(): string[] {
    return this.filterData.terrains.filter(terrain => terrain.checked).map(terrain => terrain.name);
  }

  onSearchTermChange(searchTerm: string) {
    this.filteredRaces = searchTerm ? this.fuse.search(searchTerm).map(result => result.item) : this.allRaces;
  }

  onFilterChange() {
    const selectedTerrains = this.getSelectedTerrainNames();
    if (selectedTerrains.length === 0) {
      this.filteredRaces = this.allRaces;
    }
    this.filteredRaces = this.allRaces.filter(race => race.terrainTags.some(tag => selectedTerrains.includes(tag)));
    this.filteredRaces = this.filteredRaces.filter(obj =>
      obj.distance >= this.filterData.selectedMinDistance && obj.distance <= this.filterData.selectedMaxDistance &&
      obj.elevation >= this.filterData.selectedMinElevation && obj.elevation <= this.filterData.selectedMaxElevation
    )
  }

  openDialog() {
    const dialogRef = this.dialog.open(
      AdvancedSearchComponent, {
        data: this.filterData
      }
    )
    dialogRef.afterClosed().subscribe(
      {
        next: (result: FilterData) => {
           this.filterData = result;
           this.onFilterChange();
        }
      }
    )
  }
  updateCurrentRaces(pageIndex: number = 0) {
    const start = pageIndex * this.pageSize;
    const end = start + this.pageSize;
    this.currentRaces = this.filteredRaces.slice(start, end);
  }
  pageEvent($event: PageEvent) {
    console.log($event);

    this.pageSize = $event.pageSize;
    this.updateCurrentRaces($event.pageIndex);
  }
}
