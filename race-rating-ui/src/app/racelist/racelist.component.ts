import {Component, OnInit, ViewChild} from '@angular/core';
import {RaceService} from "./race.service";
import {NgForOf} from "@angular/common";
import {RaceSummaryDto} from "./race-list.model";
import {SearchBarComponent} from "./search-bar/search-bar.component";
import {MatSelectModule} from "@angular/material/select";
import {MatDividerModule} from "@angular/material/divider";
import {MatSliderModule} from "@angular/material/slider";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {FormsModule} from "@angular/forms";
import Fuse from "fuse.js";
import {MatPaginator, MatPaginatorModule, PageEvent} from "@angular/material/paginator";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {RaceListCustomCardComponent} from "./race-list-custom-card/race-list-custom-card.component";
import {MatButtonModule} from "@angular/material/button";
import {combineLatest, map} from "rxjs";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {MatIconModule} from "@angular/material/icon";

interface SortOptions {
  value: string;
  viewValue: string;
}

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
    MatButtonModule,
    MatButtonToggleModule,
    MatIconModule
  ],
  templateUrl: './racelist.component.html',
  styleUrl: './racelist.component.scss'
})
export class RacelistComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  allRaces: RaceSummaryDto[] = [];
  filteredRaces: RaceSummaryDto[] = [];
  racesOnCurrentPage: RaceSummaryDto[] = [];
  currentSearchTerm: string = '';
  private pageFromQueryParams: number = 0;
  pageSize = 10;
  pageSizeOptions: number[] = [10, 25, 50];
  fuse!: Fuse<RaceSummaryDto>;
  fuseOptions = {
    keys: ["name"],
    includeScore: true,
    threshold: 0.3,
    distance: 100,
    useExtendedSearch: true
  };

  // sorting
  sortOptions: SortOptions[] = [
    {value: 'rating', viewValue: 'Rating'},
    {value: 'votes', viewValue: 'Votes'},
    {value: 'comments', viewValue: 'Comments'},
  ];
  sortField: string = 'rating';     // default sort type
  sortDirection: 'asc' | 'desc' = 'desc'; // default direction


  constructor(
    private raceService: RaceService,
    private router: Router,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    this.raceService.fetchAllRaces().subscribe((data) => {
      this.allRaces = data;

      // Only set filteredRaces if it's not already filtered
      if (!this.filteredRaces.length) {
        this.filteredRaces = [...this.allRaces];
        this.filteredRaces.sort((a, b) => b.ratingsCount - a.ratingsCount);
      }

      this.fuse = new Fuse(this.allRaces, this.fuseOptions);

      // Listen to queryParams separately
      this.route.queryParams.subscribe(params => {
        const page = +params['page'] || 1;
        this.pageFromQueryParams = page - 1;

        Promise.resolve().then(() => {
          if (this.paginator) {
            this.paginator.pageIndex = page - 1;
          }
          this.updateCurrentRaces(page - 1);
        });
      });
    });
  }

  onSearchTermChange(searchTerm: string) {
    this.currentSearchTerm = searchTerm;
    this.updateFilteredRaces();

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: 1 },
      queryParamsHandling: 'merge',
    });
  }

  updateCurrentRaces(pageIndex: number = 0) {
    const start = pageIndex * this.pageSize;
    const end = start + this.pageSize;
    this.racesOnCurrentPage = this.filteredRaces.slice(start, end);
  }

  pageEvent($event: PageEvent) {
    this.pageSize = $event.pageSize;
    this.updateCurrentRaces($event.pageIndex);
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: $event.pageIndex + 1 },
      queryParamsHandling: 'merge',
    });
  }


  onSortTypeChange(selectedField: string) {
    this.sortField = selectedField;
    console.log(this.sortDirection)
    this.updateFilteredRaces(true);
  }

  onSortDirectionChange(direction: 'asc' | 'desc') {
    this.sortDirection = direction;
    this.updateFilteredRaces(true);
  }

  updateFilteredRaces(resetToPage1: boolean = false) {
    // 1. Search
    const searchResults = this.currentSearchTerm
      ? this.fuse.search(this.currentSearchTerm).map(res => res.item)
      : [...this.allRaces];

    // 2. Sort
    const dir = this.sortDirection === 'asc' ? 1 : -1;

    if (this.sortField === 'rating') {
      searchResults.sort((a, b) => (a.averageRating - b.averageRating) * dir);
    } else if (this.sortField === 'votes') {
      searchResults.sort((a, b) => (a.ratingsCount - b.ratingsCount) * dir);
    } else if (this.sortField === 'comments') {
      searchResults.sort((a, b) => (a.totalComments - b.totalComments) * dir);
    }

    // 3. Assign
    this.filteredRaces = searchResults;
    this.updateCurrentRaces(0);

    this.filteredRaces = searchResults;
    this.updateCurrentRaces(0);

    if (resetToPage1) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { page: 1 },
        queryParamsHandling: 'merge',
      });
    }
  }

}
