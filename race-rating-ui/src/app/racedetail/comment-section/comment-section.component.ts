import {Component, Input, OnInit} from '@angular/core';
import {RaceComment} from "./comment/race-comment.model";
import {dummyComments} from "./dummyComments";
import {CommentComponent} from "./comment/comment.component";
import {NgForOf} from "@angular/common";
import {CommentFormComponent} from "./comment-form/comment-form.component";
import {CommentService} from "./comment.service";

@Component({
  selector: 'app-comment-section',
  standalone: true,
  imports: [
    CommentComponent,
    NgForOf,
    CommentFormComponent
  ],
  templateUrl: './comment-section.component.html',
  styleUrl: './comment-section.component.scss'
})
export class CommentSectionComponent implements OnInit{
  @Input() raceId!: number;
  comments: RaceComment[] = dummyComments;
  constructor(private commentService: CommentService) {
  }
  ngOnInit(): void {
    this.commentService.fetchCommentsByRaceId(this.raceId).subscribe(
      comments => {
        this.comments = comments;
      }
    )

  }

}
