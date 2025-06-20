import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "./auth.service";

@Component({
  selector: 'app-oauth2-redirect-handler',
  standalone: true,
  imports: [],
  templateUrl: './oauth2-redirect-handler.component.html',
  styleUrl: './oauth2-redirect-handler.component.scss'
})
export class  OAuth2RedirectHandlerComponent implements OnInit{
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {
  }
  ngOnInit() {
    this.authService.storeUserInformation().subscribe({
      next: () => {
        console.log('User information stored');
      },
      error: (err) => {
        console.log('Error storing user information', err);
      }
    })
    this.router.navigate(['/']); // Redirect to home
  }

}
