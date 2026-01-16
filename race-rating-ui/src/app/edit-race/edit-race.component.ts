import {Component, OnInit} from '@angular/core';
import {RaceService} from "../racelist-legacy/race.service";
import {ActivatedRoute} from "@angular/router";
import {EMPTY, mergeMap, switchMap} from "rxjs";
import {RaceListModel} from "../racelist-legacy/race-list.model";
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
import {NgForOf, NgIf} from "@angular/common";
import {CreateRaceEventModel} from "../create-race/create-race-event.model";
import {ToastrService} from "ngx-toastr";
import {TOASTR_ERROR_HEADER, TOASTR_SUCCESS_HEADER} from "../constants";

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
    MatIconModule,
    NgIf
  ],
  templateUrl: './edit-race.component.html',
  styleUrl: './edit-race.component.scss'
})
export class EditRaceComponent implements OnInit {
  editRaceForm: FormGroup = new FormGroup({});
  id: string | null = null;
  race!: RaceListModel;
  selectedFiles: File[] = [];
  presignedUrls: Map<string, string> = new Map<string, string>();
  s3Objects?: S3objectModel[];
  deletedS3Objects: S3objectModel[] = [];
  raceEventModel: CreateRaceEventModel = new CreateRaceEventModel();
  logoPreviewUrl: string | null = null;
  newLogoFile?: File;
  isUploadingLogo = false;
  constructor(
    private raceService: RaceService,
    private route: ActivatedRoute,
    private s3Service: S3Service,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.editRaceForm = new FormGroup({
      name: new FormControl(''),
      description: new FormControl(''),
      latitude: new FormControl(''),
      longitude: new FormControl(''),
      websiteUrl: new FormControl(''),
      logoUrl: new FormControl(''),
      eventDate: new FormControl('')
    })
    this.editRaceForm.valueChanges.subscribe(values => {
      this.raceEventModel = values;
    });

    this.route.paramMap.pipe(
      switchMap(params => {
        this.id = params.get('id');
        return this.id ? this.raceService.fetchById(this.id) : EMPTY;
      }),
      mergeMap(raceData => {
        // Process race data
        this.race = raceData;

        raceData.eventDate = new Date(raceData.eventDate);
        this.editRaceForm.patchValue(raceData);
        this.logoPreviewUrl = raceData.logoUrl;

        console.log(this.editRaceForm.getRawValue())
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
  onLogoSelected(event: any) {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }
    this.newLogoFile = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      this.logoPreviewUrl = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }

  uploadLogo() {
    if (!this.id || !this.newLogoFile) {
      return;
    }
    this.isUploadingLogo = true;
    this.raceService.uploadRaceLogo(this.id, this.newLogoFile).subscribe({
      next: (updatedRace) => {
        this.logoPreviewUrl = updatedRace.logoUrl;
        if (this.race) {
          this.race.logoUrl = updatedRace.logoUrl;
        }
        this.editRaceForm.patchValue({logoUrl: updatedRace.logoUrl});
        this.newLogoFile = undefined;
        this.isUploadingLogo = false;
        this.toastr.success('Logo updated', TOASTR_SUCCESS_HEADER);
      },
      error: () => {
        this.isUploadingLogo = false;
        this.toastr.error('Error updating logo!', TOASTR_ERROR_HEADER);
      }
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


  async uploadFilesToS3() {
    if (this.presignedUrls.size === 0) {
      return;
    }
    for (let [key, presignedUrl] of this.presignedUrls.entries()) {
      let pathSegments = key.split('/');
      let fileName = pathSegments[pathSegments.length - 1];
      let file = this.selectedFiles.find(file => file.name === fileName);
      if (file) {
        this.s3Service.uploadFileToS3(file, presignedUrl).subscribe({
          next: () => this.toastr.success('Files successfully uploaded', TOASTR_SUCCESS_HEADER),
          error: () => {
            this.toastr.error('Error uploading files!', TOASTR_ERROR_HEADER)
          }
        });
      }
    }
  }
  onFileSelected(event: any) {
    this.selectedFiles = Array.from(event.target.files);
    const fileKeys = this.selectedFiles.map((file, index) => {
      return `resources/images/${this.id}/${file.name}`;
    });
    this.s3Service.getPresignedUrls(fileKeys).subscribe({
      next: presignedUrls => {
        this.presignedUrls = presignedUrls;
        console.log(this.presignedUrls);
      },
      error: err => console.error(err)
    });
  }
  onSubmit() {
    this.uploadFilesToS3();
    this.raceService.editRace(this.id!, this.raceEventModel).subscribe({
      next: () => this.toastr.success('Race successfully updated!', TOASTR_SUCCESS_HEADER),
      error: () => this.toastr.error('Error updating race!', TOASTR_ERROR_HEADER)
    });

  }
}
