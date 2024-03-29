import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Terrain} from "../racelist.component";
import {MatSliderModule} from "@angular/material/slider";
import {MatButtonModule} from "@angular/material/button";
import {filter} from "rxjs";
import {
  FILTER_MAXIMUM_DISTANCE,
  FILTER_MAXIMUM_ELEVATION,
  FILTER_MINIMAL_DISTANCE,
  FILTER_MINIMAL_ELEVATION, TERRAINS
} from "../../constants";
import {MatCheckboxChange, MatCheckboxModule} from "@angular/material/checkbox";


export interface FilterData {
  terrains: Terrain[],
  selectedMinElevation: number,
  selectedMaxElevation: number,
  selectedMinDistance: number,
  selectedMaxDistance: number
}
@Component({
  selector: 'app-advanced-search',
  standalone: true,
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
    MatSliderModule,
    MatButtonModule,
    MatCheckboxModule
  ],
  templateUrl: './advanced-search.component.html',
  styleUrl: './advanced-search.component.scss'
})
export class AdvancedSearchComponent {
  filterData: FilterData;
  protected readonly FILTER_MINIMAL_DISTANCE = FILTER_MINIMAL_DISTANCE;
  protected readonly FILTER_MAXIMUM_DISTANCE = FILTER_MAXIMUM_DISTANCE;
  constructor(
    public dialogRef: MatDialogRef<AdvancedSearchComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FilterData,
  ) {
    console.log(data);
    this.filterData = data;
    // this.terrains = data;
    dialogRef.backdropClick().subscribe(() => {
      this.closeDialogWithResult();
    });
    dialogRef.keydownEvents().subscribe(event => {
      if (event.key === 'Escape') {
        this.closeDialogWithResult();
      }
    });
  }

  closeDialogWithResult() {
    this.dialogRef.close(this.filterData);
  }

  resetFilters() {
    this.filterData = {
      terrains: TERRAINS.map(t =>
        {
          return {
            name: t.name,
            color: t.color,
            checked: true
          }
        }
      ),
      selectedMinDistance: FILTER_MINIMAL_DISTANCE,
      selectedMaxDistance: FILTER_MAXIMUM_DISTANCE,
      selectedMinElevation: FILTER_MINIMAL_ELEVATION,
      selectedMaxElevation: FILTER_MAXIMUM_ELEVATION
    };
  }

  onFilterChange($event: MatCheckboxChange) {
   // maybe filter results on the background while dialog is open?
  }

  protected readonly FILTER_MAXIMUM_ELEVATION = FILTER_MAXIMUM_ELEVATION;
  protected readonly FILTER_MINIMAL_ELEVATION = FILTER_MINIMAL_ELEVATION;
}
