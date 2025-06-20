import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "./auth.service";
import {UserModel} from "./stored-user.model";
import { HttpResponse } from '@angular/common/http';

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
    private route: ActivatedRoute,
    private router: Router,
  ) {
  }
  ngOnInit() {
    const accessToken = this.route.snapshot.queryParamMap.get('accessToken');
    const refreshToken = this.route.snapshot.queryParamMap.get('refreshToken');

    if (accessToken) {
      this.authService.getCookies({ accessToken, refreshToken })
        .subscribe({
          next: (response: HttpResponse<UserModel>) => {
            const userDto = response.body;
            const expiresAt = response.headers.get('access-token-expires-at');

            this.authService.storeUserModel(userDto);
            if (expiresAt){
              this.authService.storeAccessTokenExpiration(expiresAt);
            }
            this.router.navigate(['/'])
          }, // Redirect to home, // or any secure route
          error: err => console.error('Cookie setup failed', err)
        });
    } else {
      console.error('No token found in query params');
    }
  }

}
