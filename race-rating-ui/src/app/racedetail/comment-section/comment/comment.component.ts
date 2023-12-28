import {Component, Input} from '@angular/core';
import {RaceListModel} from "../../../racelist/RaceListModel";
import {RaceComment} from "./race-comment.model";

@Component({
  selector: 'app-comment',
  standalone: true,
  imports: [],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss'
})
export class CommentComponent {
  @Input() raceComment!: RaceComment
}
