import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Terrain} from "../racelist.component";



@Component({
  selector: 'app-advanced-search',
  standalone: true,
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './advanced-search.component.html',
  styleUrl: './advanced-search.component.scss'
})
export class AdvancedSearchComponent {
  terrains: Terrain[];
  constructor(
    public dialogRef: MatDialogRef<AdvancedSearchComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Terrain[],
  ) {
    this.terrains = data;
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
    this.dialogRef.close(this.terrains);
  }

  onFilterChange($event: Event) {
   // maybe filter results on the background while dialog is open?
  }
}
