import { Component } from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {AuthService} from "../oauth2-redirect-handler/auth.service";
import {RouterLink} from "@angular/router";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {TOASTR_ERROR_HEADER, TOASTR_SUCCESS_HEADER} from "../../constants";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatCardModule,
    MatIconModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterLink,
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  hide = true;
  myForm: FormGroup;
  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private toastr: ToastrService
  ) {
    this.myForm = this.fb.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    })
  }
  onSubmit() {
    if (this.myForm.valid) {
      this.authService.signIn(this.myForm.value).subscribe(
        {
          next: (response) => {
            console.log(response);
            this.authService.handleLogin(response.token);
            this.toastr.success('You have successfully logged in.', TOASTR_SUCCESS_HEADER)
            console.log('User signed up');
          },
          error: (error) => {
            this.toastr.error('Bad credentials.', TOASTR_ERROR_HEADER)
          }
        }
      );
    }
  }

  getGoogleAuthUrl() {
    return this.authService.getGoogleAuthUrl();
  }




}
