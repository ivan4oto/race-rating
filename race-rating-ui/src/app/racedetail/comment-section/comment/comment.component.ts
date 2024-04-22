import {Component, Input} from '@angular/core';
import {RaceListModel} from "../../../racelist/race-list.model";
import {RaceComment} from "./race-comment.model";
import {DatePipe, NgStyle} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-comment',
  standalone: true,
  imports: [
    DatePipe,
    MatIconModule,
    MatButtonModule,
    NgStyle
  ],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss'
})
export class CommentComponent {
  nonVotedIconColor: string = '#696969';
  votedIconColor: string = '#000000';

  @Input() raceComment!: RaceComment
}
