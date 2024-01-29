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
  styleUrl: './carousel.component.scss'
})
export class CarouselComponent implements OnInit{
  id: string | null = null;
  currentSlideIndex = 0;
  imgSlides: S3objectModel[] = [];
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
          this.imgSlides = value
        },
        error: err => console.log(err)
      }
    )
  }
  previousSlide() {
    console.log('previous slide');
    this.currentSlideIndex = (this.currentSlideIndex + this.imgSlides.length - 1) % this.imgSlides.length;
  }

  nextSlide() {
    console.log('next slide');
    this.currentSlideIndex = (this.currentSlideIndex + 1) % this.imgSlides.length;
  }

}
