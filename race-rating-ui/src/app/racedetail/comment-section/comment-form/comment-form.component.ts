import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {CommentService} from "../comment.service";
import {RaceComment} from "../comment/race-comment.model";
import {AuthService} from "../../../auth/oauth2-redirect-handler/auth.service";
import {ToastrService} from "ngx-toastr";
import {TOASTR_SUCCESS_HEADER} from "../../../constants";

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
  constructor(
    private commentService: CommentService,
    private authService: AuthService,
    private toastr: ToastrService
    ) {
  }

  onSubmit() {
    if (this.commentText.trim().length === 0) {
      this.toastr.error('Comment cannot be empty!', TOASTR_SUCCESS_HEADER)
      return;
    }
    this.commentService.sendComment(this.raceId, this.commentText).subscribe(
      {
        next: (comment: RaceComment) => {
          this.commentAdded.emit(comment);
          this.toastr.success('Comment successfully added!', TOASTR_SUCCESS_HEADER);
          this.authService.addRaceToCommented(this.raceId);
        },
        error: (error) => {
          console.log(error)
          this.toastr.error('Error while adding comment!', TOASTR_SUCCESS_HEADER);
        }
      }
    )
    this.commentText = '';
  }
}
