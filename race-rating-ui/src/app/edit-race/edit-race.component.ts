import {Component, OnInit} from '@angular/core';
import {RaceService} from "../racelist/race.service";
import {ActivatedRoute} from "@angular/router";
import {EMPTY, switchMap} from "rxjs";
import {RaceListModel} from "../racelist/race-list.model";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {MatDatepickerModule} from "@angular/material/datepicker";

@Component({
  selector: 'app-edit-race',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule,
    MatButtonModule,
    MatOptionModule,
    MatSelectModule,
    MatDatepickerModule
  ],
  templateUrl: './edit-race.component.html',
  styleUrl: './edit-race.component.scss'
})
export class EditRaceComponent implements OnInit {
  editRaceForm: FormGroup = new FormGroup({});
  id: string | null = null;
  race!: RaceListModel;
  constructor(
    private raceService: RaceService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.editRaceForm = new FormGroup({
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

    this.route.paramMap.pipe(
      switchMap(params => {
        this.id = params.get('id');
        return this.id ? this.raceService.fetchById(this.id) : EMPTY;
      })
    ).subscribe(
      {
        next: value => {
          value.eventDate = new Date(value.eventDate);
          this.editRaceForm.patchValue(value)
        },
        error: err => console.log(err)
      }
    )
  }

  onSubmit() {

  }
}
