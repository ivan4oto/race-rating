import {Component, OnInit} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {S3objectModel} from "../misc-models/s3object.model";
import {S3Service} from "../help-services/s3.service";
import {EMPTY, switchMap} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-carousel',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    NgIf
  ],
  templateUrl: './carousel.component.html',
  styleUrls: [
    './carousel.component.scss'
  ]
})
export class CarouselComponent implements OnInit{
  id: string | null = null;
  currentSlideIndex = 0;
  s3ImageObjects: S3objectModel[] = [];
  images: HTMLImageElement[] = [];

  constructor(
    private s3Service: S3Service,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        this.id = params.get('id');
        return this.id ? this.s3Service.listImages(this.id) : EMPTY;
      })
    ).subscribe(
      {
        next: value => {
          this.s3ImageObjects = value
          this.images = this.s3ImageObjects.map(s3Object => {
            const image = new Image();
            image.src = s3Object.fullUrl;
            return image;
          });
          console.log(this.images)

        },
        error: err => console.log(err)
      }
    )
  }

  get currentSlide(): HTMLImageElement {
    return this.images[this.currentSlideIndex];
  }

  prev() {
    this.currentSlideIndex = (this.currentSlideIndex - 1 + this.s3ImageObjects.length) % this.s3ImageObjects.length;
  }

  next() {
    this.currentSlideIndex = (this.currentSlideIndex + 1) % this.s3ImageObjects.length;
  }
}
