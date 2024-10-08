import {Component} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../oauth2-redirect-handler/auth.service";
import {ToastrService} from "ngx-toastr";
import {TOASTR_ERROR_HEADER, TOASTR_SUCCESS_HEADER} from "../../constants";

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {
  hide = true;
  myForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService
  ) {
    this.myForm = this.fb.group({
      username: ['', Validators.required],
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.myForm.valid) {
      this.authService.signUp(this.myForm.value).subscribe(
        {
          next: (token) => {
            console.log(token);
            this.authService.handleLogin(token);
            this.toastr.success('Login success!', TOASTR_SUCCESS_HEADER)
            console.log('User signed up');
          },
          error: (error) => {
            if (error.error) {
              const message = error.error;
              switch (true) {
                case message.includes('Email'):
                  this.toastr.error('The email is already in use. Please use another email.', TOASTR_ERROR_HEADER);
                  break;
                case message.includes('Username'):
                  this.toastr.error('The username is already taken. Please choose a different username.', TOASTR_ERROR_HEADER);
                  break;
                default:
                  this.toastr.error('There was an issue with your signup. Please try again.', TOASTR_ERROR_HEADER);
                  break;
              }
            } else {
              this.toastr.error('An error occurred. Please try again.', TOASTR_ERROR_HEADER);
            }
          }
        }
      );
    }
  }
}
