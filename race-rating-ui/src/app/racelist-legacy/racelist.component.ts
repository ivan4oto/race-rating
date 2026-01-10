import {Component, OnInit, ViewChild} from '@angular/core';
import {RaceService} from "./race.service";
import {NgForOf, NgIf} from "@angular/common";
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
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {MatIconModule} from "@angular/material/icon";
import {MatProgressBarModule} from "@angular/material/progress-bar";

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
    MatIconModule,
    NgIf,
    MatProgressBarModule
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
  isLoading: boolean = true;
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
  sortField: string = '';     // no default; only apply when user selects
  sortDirection: 'asc' | 'desc' = 'desc'; // default direction
  private isSortActive = false;


  constructor(
    private raceService: RaceService,
    private router: Router,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    this.raceService.fetchAllRaces().subscribe({
      next: (data) => {
        this.allRaces = data;

        // Only set filteredRaces if it's not already filtered
        if (!this.filteredRaces.length) {
          // Initial order: recent-first; others by ratingsCount desc
          this.filteredRaces = this.orderRecentFirst(this.allRaces, true);
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
      },
      error: () => {
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
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
    this.isSortActive = !!this.sortField;
    this.updateFilteredRaces(true);
  }

  onSortDirectionChange(direction: 'asc' | 'desc') {
    this.sortDirection = direction;
    if (this.sortField) {
      this.isSortActive = true;
      this.updateFilteredRaces(true);
    }
  }

  updateFilteredRaces(resetToPage1: boolean = false) {
    // 1. Search
    const searchResults = this.currentSearchTerm
      ? this.fuse.search(this.currentSearchTerm).map(res => res.item)
      : [...this.allRaces];

    // 2. Order
    let ordered: RaceSummaryDto[] = searchResults;
    if (this.isSortActive && this.sortField) {
      const dir = this.sortDirection === 'asc' ? 1 : -1;
      if (this.sortField === 'rating') {
        ordered = [...searchResults].sort((a, b) => (a.averageRating - b.averageRating) * dir);
      } else if (this.sortField === 'votes') {
        ordered = [...searchResults].sort((a, b) => (a.ratingsCount - b.ratingsCount) * dir);
      } else if (this.sortField === 'comments') {
        ordered = [...searchResults].sort((a, b) => (a.totalComments - b.totalComments) * dir);
      }
    } else {
      // Keep recent races on top; preserve search relevance for others
      ordered = this.orderRecentFirst(searchResults, false);
    }

    // 3. Assign and paginate
    this.filteredRaces = ordered;
    this.updateCurrentRaces(0);

    if (resetToPage1) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: {page: 1},
        queryParamsHandling: 'merge',
      });
    }
  }

  private isRecent(date: Date | string | undefined | null): boolean {
    if (!date) return false;
    const now = new Date();
    const target = new Date(date);
    const sevenDaysMs = 7 * 24 * 60 * 60 * 1000;
    return Math.abs(target.getTime() - now.getTime()) <= sevenDaysMs;
  }

  private orderRecentFirst(list: RaceSummaryDto[], sortOthersByRatings: boolean): RaceSummaryDto[] {
    const now = new Date().getTime();
    const recent: RaceSummaryDto[] = [];
    const others: RaceSummaryDto[] = [];

    for (const r of list) {
      if (r?.eventDate && this.isRecent(r.eventDate)) {
        recent.push(r);
      } else {
        others.push(r);
      }
    }

    // Sort recent by closeness to today (ascending)
    recent.sort((a, b) => {
      const da = Math.abs(new Date(a.eventDate).getTime() - now);
      const db = Math.abs(new Date(b.eventDate).getTime() - now);
      return da - db;
    });

    if (sortOthersByRatings) {
      others.sort((a, b) => b.ratingsCount - a.ratingsCount);
    }

    return [...recent, ...others];
  }

}
