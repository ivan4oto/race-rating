import {Component, OnInit} from '@angular/core';
import {RaceService} from "../racelist/race.service";
import {ActivatedRoute} from "@angular/router";
import {EMPTY, mergeMap, switchMap} from "rxjs";
import {RaceListModel} from "../racelist/race-list.model";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatLineModule, MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {S3Service} from "../help-services/s3.service";
import {S3objectModel} from "../misc-models/s3object.model";
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-edit-race',
  standalone: true,
  imports: [
    NgForOf,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule,
    MatButtonModule,
    MatOptionModule,
    MatSelectModule,
    MatDatepickerModule,
    MatListModule,
    MatLineModule,
    MatIconModule
  ],
  templateUrl: './edit-race.component.html',
  styleUrl: './edit-race.component.scss'
})
export class EditRaceComponent implements OnInit {
  editRaceForm: FormGroup = new FormGroup({});
  id: string | null = null;
  race!: RaceListModel;
  s3Objects?: S3objectModel[];
  deletedS3Objects: S3objectModel[] = [];
  constructor(
    private raceService: RaceService,
    private route: ActivatedRoute,
    private s3Service: S3Service
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
      }),
      mergeMap(raceData => {
        // Process race data
        raceData.eventDate = new Date(raceData.eventDate);
        this.editRaceForm.patchValue(raceData);

        // Now fetch S3 objects if race ID is available
        return this.id ? this.s3Service.listImages(this.id) : EMPTY;
      })
    ).subscribe({
      next: s3Data => {
        this.s3Objects = s3Data;
      },
      error: err => console.error(err)
    });
  }
  removeS3Object(index: number): void {
    if (!this.s3Objects) {
      return;
    }
    this.s3Objects.splice(index, 1);
    // Optionally, you can keep track of deleted objects to handle them on submission
    // this.deletedS3Objects.push(s3Object);
  }
  onSubmit() {

  }
}
