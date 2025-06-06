import {Component, OnInit} from '@angular/core';
import {RaceService} from "./race.service";
import {NgForOf} from "@angular/common";
import {RaceListModel} from "./race-list.model";
import {SearchBarComponent} from "./search-bar/search-bar.component";
import {MatSelectModule} from "@angular/material/select";
import {MatDividerModule} from "@angular/material/divider";
import {MatSliderModule} from "@angular/material/slider";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {FormsModule} from "@angular/forms";
import Fuse from "fuse.js";
import {MatPaginatorModule, PageEvent} from "@angular/material/paginator";
import {RouterLink} from "@angular/router";
import {RaceListCustomCardComponent} from "./race-list-custom-card/race-list-custom-card.component";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-racelist',
  standalone: true,
  imports: [
    NgForOf,
    SearchBarComponent,
    MatSelectModule,
    MatDividerModule,
    MatSliderModule,
    MatCheckboxModule,
    FormsModule,
    MatPaginatorModule,
    RouterLink,
    RaceListCustomCardComponent,
    MatButtonModule
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
    threshold: 0.3,
    distance: 100,
    useExtendedSearch: true
  };

  constructor(
    private raceService: RaceService,
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

  onSearchTermChange(searchTerm: string) {
    this.filteredRaces = searchTerm ? this.fuse.search(searchTerm).map(result => result.item) : this.allRaces;
    this.updateCurrentRaces();
  }

  updateCurrentRaces(pageIndex: number = 0) {
    const start = pageIndex * this.pageSize;
    const end = start + this.pageSize;
    this.currentRaces = this.filteredRaces.slice(start, end);
  }
  pageEvent($event: PageEvent) {
    this.pageSize = $event.pageSize;
    this.updateCurrentRaces($event.pageIndex);
  }
}
