import {Component, Input} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {RaceListModel} from "../RaceListModel";
import {RatingDisplayComponent} from "../rating-display/rating-display.component";

@Component({
  selector: 'app-racelist-card',
  standalone: true,
  imports: [
    MatCardModule,
    RatingDisplayComponent
  ],
  templateUrl: './racelist-card.component.html',
  styleUrl: './racelist-card.component.scss'
})
export class RacelistCardComponent {
    @Input() data!: RaceListModel
}
