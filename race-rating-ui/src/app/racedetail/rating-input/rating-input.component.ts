import { Component } from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgForOf} from "@angular/common";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-rating-input',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    MatButtonModule
  ],
  templateUrl: './rating-input.component.html',
  styleUrl: './rating-input.component.scss'
})
export class RatingInputComponent {
  labels: string[] = ['Организация', 'Трасе', 'Атмосфера', 'Локация', 'Такса'];
  ratings: number[] = [0, 0, 0, 0, 0];
  submitRatings() {
    console.log(this.ratings)
  }

  setRating(index: number, value: number) {
    this.ratings[index] = value;
  }
  isSelected(index: number, value: number): boolean {
    return this.ratings[index] === value;
  }
}
