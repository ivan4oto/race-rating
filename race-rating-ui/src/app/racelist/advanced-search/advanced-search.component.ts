import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Terrain} from "../racelist.component";
import {MatSliderModule} from "@angular/material/slider";
import {MatButtonModule} from "@angular/material/button";
import {filter} from "rxjs";


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
    MatButtonModule
  ],
  templateUrl: './advanced-search.component.html',
  styleUrl: './advanced-search.component.scss'
})
export class AdvancedSearchComponent {
  filterData: FilterData;
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

  onFilterChange($event: Event) {
   // maybe filter results on the background while dialog is open?
  }

  protected readonly filter = filter;
}
