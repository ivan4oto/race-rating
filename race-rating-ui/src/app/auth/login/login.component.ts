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
  constructor(private authService: AuthService, private fb: FormBuilder) {
    this.myForm = this.fb.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    })
  }
  onSubmit() {
    if (this.myForm.valid) {
      this.authService.signin(this.myForm.value).subscribe(
        {
          next: (response) => {
            console.log(response);
            this.authService.handleLogin(response.token);
            console.log('User signed up');
          },
          error: (error) => {
            console.log(error);
          }
        }
      );
    }
  }

  getGoogleAuthUrl() {
    return this.authService.getGoogleAuthUrl();
  }




}
