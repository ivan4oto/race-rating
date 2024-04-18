import {Component, Input} from '@angular/core';
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {CommentService} from "../comment.service";

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
  @Input() raceId!: number;
  constructor(private commentService: CommentService) {
  }

  onSubmit() {
    this.commentService.sendComment(this.raceId, this.commentText).subscribe(
      comment => {
        console.log('Comment sent:', comment);
      }
    )
    console.log('Comment successfully saved:', this.commentText);
  }
}
