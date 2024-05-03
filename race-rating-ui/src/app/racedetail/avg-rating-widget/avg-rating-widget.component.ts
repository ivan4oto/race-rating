import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-avg-rating-widget',
  standalone: true,
  imports: [],
  templateUrl: './avg-rating-widget.component.html',
  styleUrl: './avg-rating-widget.component.scss'
})
export class AvgRatingWidgetComponent {
  @Input() avgRating!: number;
  @Input() totalRatings!: number;

}
