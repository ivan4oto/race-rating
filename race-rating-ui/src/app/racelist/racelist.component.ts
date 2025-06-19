import {Component, OnInit, ViewChild} from '@angular/core';
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
import {MatPaginator, MatPaginatorModule, PageEvent} from "@angular/material/paginator";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {RaceListCustomCardComponent} from "./race-list-custom-card/race-list-custom-card.component";
import {MatButtonModule} from "@angular/material/button";
import {combineLatest, map} from "rxjs";
import {AuthService} from "../auth/oauth2-redirect-handler/auth.service";

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
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  allRaces: RaceListModel[] = [];
  filteredRaces: RaceListModel[] = [];
  currentRaces: RaceListModel[] = [];
  private pageFromQueryParams: number = 0;
  pageSize = 10;
  pageSizeOptions: number[] = [10, 25, 50];
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
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
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
        this.updateCurrentRaces(page - 1);
        this.fuse = new Fuse(this.allRaces, this.fuseOptions);
        Promise.resolve().then(() => {
          if (this.paginator) {
            this.paginator.pageIndex = page - 1;
          }
        });
      });
    this.authService.storeUserInformation().subscribe({
      next: () => {
        console.log('User information stored');
      },
      error: (err) => {
        console.log('Error storing user information', err);
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
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: $event.pageIndex + 1 },
      queryParamsHandling: 'merge',
    });
  }
}
