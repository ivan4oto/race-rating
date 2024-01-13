import { Component } from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-carousel',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule
  ],
  templateUrl: './carousel.component.html',
  styleUrl: './carousel.component.scss'
})
export class CarouselComponent {
  currentSlideIndex = 0;
  slides = [
    { img: 'assets/images/image1.jpg', caption: 'Caption 1' },
    { img: 'assets/images/image2.jpg', caption: 'Caption 2' },
    // Add more slides as needed
  ];

  previousSlide() {
    this.currentSlideIndex = (this.currentSlideIndex + this.slides.length - 1) % this.slides.length;
  }

  nextSlide() {
    this.currentSlideIndex = (this.currentSlideIndex + 1) % this.slides.length;
  }
}
