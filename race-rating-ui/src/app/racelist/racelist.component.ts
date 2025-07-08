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
    combineLatest([
      this.raceService.fetchAllRaces(),
      this.route.queryParams
    ])
      .pipe(
        map(([data, params]) => {
          const page = +params['page'] || 1;
          this.pageFromQueryParams = page - 1;
          return { data, page };
        })
      )
      .subscribe(({ data, page }) => {
        this.allRaces = data;
        this.filteredRaces = [...this.allRaces]; // optional: clone to avoid side effects
        this.filteredRaces.sort((a, b) => b.ratingsCount - a.ratingsCount); // sort races by highest review count
        this.updateCurrentRaces(page - 1);
        this.fuse = new Fuse(this.allRaces, this.fuseOptions);
        Promise.resolve().then(() => {
          if (this.paginator) {
            this.paginator.pageIndex = page - 1;
          }
        });
      });
  }

  onSearchTermChange(searchTerm: string) {
    this.filteredRaces = searchTerm ? this.fuse.search(searchTerm).map(result => result.item) : this.allRaces;
    this.updateCurrentRaces();
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
    console.log('Sort field selected:', selectedField);
    this.sortField = selectedField;
    // this.sortRaces();


    if (selectedField === 'rating') {
      this.filteredRaces.sort((a, b) => b.averageRating - a.averageRating);
      this.updateCurrentRaces();
      return;
    }
    if (selectedField === 'votes') {
      this.filteredRaces.sort((a, b) => b.ratingsCount - a.ratingsCount);
      this.updateCurrentRaces();
      return;
    }

  }

  onSortDirectionChange(direction: 'asc' | 'desc') {
    this.sortDirection = direction;
    this.sortRaces();

    console.log('Sort direction selected:', direction);
  }

  sortRaces() {
    const dir = this.sortDirection === 'asc' ? 1 : -1;

    if (this.sortField === 'rating') {
      this.filteredRaces.sort((a, b) => (a.averageRating - b.averageRating) * dir);
    } else if (this.sortField === 'votes') {
      this.filteredRaces.sort((a, b) => (a.ratingsCount - b.ratingsCount) * dir);
    } else if (this.sortField === 'comments') {
      this.filteredRaces.sort((a, b) => (a.totalComments - b.totalComments) * dir);
    }

    this.updateCurrentRaces();
  }

}
