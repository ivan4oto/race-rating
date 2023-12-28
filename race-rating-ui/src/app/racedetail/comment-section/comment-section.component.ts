import { Component } from '@angular/core';
import {RaceComment} from "./comment/race-comment.model";
import {dummyComments} from "./dummyComments";
import {CommentComponent} from "./comment/comment.component";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-comment-section',
  standalone: true,
  imports: [
    CommentComponent,
    NgForOf
  ],
  templateUrl: './comment-section.component.html',
  styleUrl: './comment-section.component.scss'
})
export class CommentSectionComponent {
  comments: RaceComment[] = dummyComments;

}
