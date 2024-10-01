import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSelectModule} from "@angular/material/select";
import {MatButtonModule} from "@angular/material/button";
import {MatNativeDateModule, MatRippleModule} from "@angular/material/core";
import {CreateRaceEventModel} from "./create-race-event.model";
import {RaceService} from "../racelist/race.service";
import {RaceListModel} from "../racelist/race-list.model";
import {MatListModule} from "@angular/material/list";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {NgForOf, NgIf} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {TOASTR_ERROR_HEADER, TOASTR_SUCCESS_HEADER} from "../constants";

@Component({
  selector: 'app-create-race',
  standalone: true,
  imports: [
    MatInputModule,
    MatNativeDateModule,
    MatDatepickerModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatButtonModule,
    MatListModule,
    MatCardModule,
    MatIconModule,
    NgIf,
    NgForOf,
    MatRippleModule
  ],
  templateUrl: './create-race.component.html',
  styleUrl: './create-race.component.scss'
})
export class CreateRaceComponent implements OnInit{
  raceEventModel: CreateRaceEventModel = new CreateRaceEventModel();
  eventForm: FormGroup = new FormGroup({});
  distances = new Set<number>();
  tags = new Set<string>();
  constructor(
    private raceService: RaceService,
    private toastr: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.eventForm = new FormGroup({
      name: new FormControl(''),
      description: new FormControl(''),
      latitude: new FormControl(''),
      longitude: new FormControl(''),
      websiteUrl: new FormControl(''),
      logoUrl: new FormControl(''),
      distances: new FormControl('', [Validators.required, Validators.pattern(/^[0-9]*$/)]),
      terrainTags: new FormControl(''),
      distance: new FormControl(''),
      elevation: new FormControl(''),
      eventDate: new FormControl('')
    })
    this.eventForm.valueChanges.subscribe(values => {
      this.raceEventModel = values;
    });

  }
  submitForm() {
    if (this.distances) {
      this.raceEventModel.availableDistances = Array.from(this.distances);
    }
    if (this.tags) {
      this.raceEventModel.terrainTags = Array.from(this.tags);
    }
    console.log(this.raceEventModel);
    this.raceService.createRace(this.raceEventModel).subscribe({
        next: (createdRace: RaceListModel) => {
          console.log(createdRace);
          this.toastr.success('Race succesfully created!', TOASTR_SUCCESS_HEADER);
        },
        error: (e) => {
          console.log(e);
          this.toastr.error('Error creating race!', TOASTR_ERROR_HEADER);
        }
      }
    )
  }

  addDistance() {
    const distancesControl = this.eventForm.get('distances');
    if (distancesControl && distancesControl.valid) {
      this.distances.add(+distancesControl.value); // Convert and add to set
      distancesControl.reset(); // Reset the field
    } else {
      console.log('Invalid input'); // Handle invalid input
    }
  }

  popDistance() {
    this.distances.delete(this.distances.values().next().value);
  }

  addTag() {
    const tagsControl = this.eventForm.get('terrainTags');
    if (tagsControl && tagsControl.valid) {
      this.tags.add(tagsControl.value); // Add to set
      console.log(tagsControl.value);
      tagsControl.reset(); // Reset the field
    } else {
      console.log('Invalid input'); // Handle invalid input
    }
  }

  popTag() {
    this.tags.delete(this.tags.values().next().value);
  }

}
