import {Component, Input} from '@angular/core';
import {BrowserModule} from "@angular/platform-browser";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-rating-display',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rating-display.component.html',
  styleUrl: './rating-display.component.scss'
})
export class RatingDisplayComponent {
  @Input() rating: number = 0;

  // Function to create an array for ngFor based on the rating
  get stars(): boolean[] {
    let stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= this.rating);
    }
    console.log('push')
    return stars;
  }
}
