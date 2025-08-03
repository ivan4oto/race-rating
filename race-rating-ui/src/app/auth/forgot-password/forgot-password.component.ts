import { Component } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {RouterLink} from "@angular/router";
import {TOASTR_ERROR_HEADER, TOASTR_SUCCESS_HEADER} from "../../constants";
import {AuthService} from "../oauth2-redirect-handler/auth.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
  myForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService
    ) {
    this.myForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }
  onSubmit() {
    if (this.myForm.valid) {
      this.authService.requestPasswordReset(this.myForm.value).subscribe(
        {
          next: () => {
            this.toastr.info('We have send an email with instructions to reset, if the provided email exists.', TOASTR_SUCCESS_HEADER, {
              positionClass: 'toast-top-center'
            })
          },
          error: (e) => {
            console.log(e);
            this.toastr.error('An error occurred. Please try again.', TOASTR_ERROR_HEADER);
          }
        }
      );
    }
  }
}
