import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
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
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {
  }
  ngOnInit() {
      this.authService.storeUserInformation().subscribe(
        {
          next: (response) => {
            if (response.body) {
              console.log('Success login.')
            }
          },
          error: (err) => {
            console.log(err);
          }
        }
      );
      this.router.navigate(['/']); // Redirect to home
  }

}
