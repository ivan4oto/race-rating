import {Component, Input} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {RaceListModel} from "../race-list.model";
import {RatingDisplayComponent} from "../rating-display/rating-display.component";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-racelist-card',
  standalone: true,
  imports: [
    MatCardModule,
    RatingDisplayComponent,
    RouterLink
  ],
  templateUrl: './racelist-card.component.html',
  styleUrl: './racelist-card.component.scss'
})
export class RacelistCardComponent {
    @Input() data!: RaceListModel
}
