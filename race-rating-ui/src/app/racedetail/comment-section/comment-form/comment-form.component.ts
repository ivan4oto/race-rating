import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {CommentService} from "../comment.service";
import {RaceComment} from "../comment/race-comment.model";

@Component({
  selector: 'app-comment-form',
  standalone: true,
  imports: [
    MatInputModule,
    FormsModule,
    MatButtonModule
  ],
  templateUrl: './comment-form.component.html',
  styleUrl: './comment-form.component.scss'
})
export class CommentFormComponent {
  commentText: string = '';
  @Output() commentAdded = new EventEmitter<RaceComment>();
  @Input() raceId!: number;
  constructor(private commentService: CommentService) {
  }

  onSubmit() {
    this.commentService.sendComment(this.raceId, this.commentText).subscribe(
      comment => {
        console.log('Comment sent:', comment);
        this.commentAdded.emit(comment);
      }
    )
    this.commentText = '';
    console.log('Comment successfully saved:', this.commentText);
  }
}
