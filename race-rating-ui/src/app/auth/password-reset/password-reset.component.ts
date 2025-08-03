import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {AuthService} from '../oauth2-redirect-handler/auth.service';
import {ToastrService} from "ngx-toastr";
import {TOASTR_ERROR_HEADER, TOASTR_SUCCESS_HEADER} from "../../constants";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-password-reset',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    RouterLink,
    NgIf
  ],
  templateUrl: './password-reset.component.html',
  styleUrl: './password-reset.component.scss'
})
export class PasswordResetComponent implements OnInit {
  newPassword: string = '';
  confirmPassword: string = '';
  token: string = '';
  message: string = '';
  success: boolean = false;

  myForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
    private fb: FormBuilder
  ) {
    this.myForm = this.fb.group(
      {
        password: ['', [Validators.required, this.passwordStrengthValidator()]],
        confirmPassword: ['', [Validators.required]]
      },
      {validators: this.passwordsMatchValidator});
  }

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
  }

  onSubmit() {
    if (this.myForm.invalid) {
      this.toastr.error('There has been an error. Please enter your password again.', TOASTR_ERROR_HEADER);
      return;
    }

    const password = this.myForm.get('password')?.value;

    this.authService.resetPassword(this.token, password).subscribe({
      next: () => {
        this.success = true;
        this.message = 'Your password has been reset. You can now log in.';
        this.toastr.success(this.message, TOASTR_SUCCESS_HEADER);
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.message = err?.error || 'Reset failed. Try again.';
        this.toastr.error(this.message, TOASTR_ERROR_HEADER);
      }
    });
  }

  passwordStrengthValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const value = control.value || '';
      const strong =
        value.length >= 8 &&
        /[A-Z]/.test(value) &&
        /[a-z]/.test(value) &&
        /\d/.test(value);

      return strong ? null : {weakPassword: true};
    };
  }

  passwordsMatchValidator(group: AbstractControl): { [key: string]: any } | null {
    const password = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : {passwordsMismatch: true};
  }
}
