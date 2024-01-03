import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSelectModule} from "@angular/material/select";
import {MatButtonModule} from "@angular/material/button";
import {MatNativeDateModule} from "@angular/material/core";
import {CreateRaceEventModel} from "./create-race-event.model";

@Component({
  selector: 'app-create-race',
  standalone: true,
  imports: [
    MatInputModule,
    MatNativeDateModule,
    MatDatepickerModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatButtonModule
  ],
  templateUrl: './create-race.component.html',
  styleUrl: './create-race.component.scss'
})
export class CreateRaceComponent implements OnInit{
  raceEventModel: CreateRaceEventModel = new CreateRaceEventModel();
  eventForm: FormGroup = new FormGroup({});

  ngOnInit(): void {
    this.eventForm = new FormGroup({
      name: new FormControl(''),
      description: new FormControl(''),
      latitude: new FormControl(''),
      longitude: new FormControl(''),
      websiteUrl: new FormControl(''),
      logoUrl: new FormControl(''),
      terrain: new FormControl(''),
      distance: new FormControl(''),
      elevation: new FormControl(''),
      eventDate: new FormControl('')
    })
    this.eventForm.valueChanges.subscribe(values => {
      this.raceEventModel = values;
    });

  }
  submitForm() {
    console.log(this.raceEventModel);
    // Further submission logic goes here
  }

}
