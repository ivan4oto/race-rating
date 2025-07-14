import {Component, Input} from '@angular/core';
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
  @Input() ratingName: string = '';

  // Function to create an array for ngFor based on the rating
  get stars(): boolean[] {
    const roundedRating = Math.round(this.rating); // Rounds to nearest integer
    let stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= roundedRating);
    }
    return stars;
  }
}
