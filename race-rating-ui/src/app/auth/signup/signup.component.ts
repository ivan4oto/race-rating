import { Component } from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../oauth2-redirect-handler/auth.service";

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
  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.myForm = this.fb.group({
      username: ['', Validators.required],
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }
  onSubmit() {
    if (this.myForm.valid) {
      this.authService.signup(this.myForm.value).subscribe(
        {
          next: (token) => {
            console.log(token);
            this.authService.handleLogin(token);
            console.log('User signed up');
          },
          error: (error) => {
            console.log(error);
          }
        }
      );
    }
  }
}
