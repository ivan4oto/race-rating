import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-rating-bar',
  standalone: true,
  imports: [],
  templateUrl: './rating-bar.component.html',
  styleUrl: './rating-bar.component.scss'
})
export class RatingBarComponent {
  @Input() rating: number = 10;
}
