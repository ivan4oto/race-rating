import {Component, Input} from '@angular/core';
import {RaceListModel} from "../../../racelist/RaceListModel";
import {RaceComment} from "./race-comment.model";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-comment',
  standalone: true,
  imports: [
    DatePipe
  ],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss'
})
export class CommentComponent {
  @Input() raceComment!: RaceComment
}
