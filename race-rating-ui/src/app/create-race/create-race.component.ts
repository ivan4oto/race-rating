import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSelectModule} from "@angular/material/select";
import {MatButtonModule} from "@angular/material/button";
import {MatNativeDateModule, MatRippleModule} from "@angular/material/core";
import {CreateRaceEventModel} from "./create-race-event.model";
import {RaceService} from "../racelist-legacy/race.service";
import {RaceListModel} from "../racelist-legacy/race-list.model";
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
      eventDate: new FormControl('')
    })
    this.eventForm.valueChanges.subscribe(values => {
      this.raceEventModel = values;
    });

  }
  submitForm() {
    this.raceService.createRace(this.raceEventModel).subscribe({
        next: (createdRace: RaceListModel) => {
          this.toastr.success('Race succesfully created!', TOASTR_SUCCESS_HEADER);
        },
        error: (e) => {
          console.error(e);
          this.toastr.error('Error creating race!', TOASTR_ERROR_HEADER);
        }
      }
    )
  }
}
