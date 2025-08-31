import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {catchError, EMPTY, take} from 'rxjs';

import { AuthService } from './auth.service';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-oauth2-redirect-handler',
  standalone: true,
  imports: [
    MatProgressSpinnerModule
  ],
  templateUrl: './oauth2-redirect-handler.component.html',
  styleUrl: './oauth2-redirect-handler.component.scss'
})
export class OAuth2RedirectHandlerComponent implements OnInit {
  constructor(
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    const qp = this.route.snapshot.queryParamMap;
    const accessToken = qp.get('accessToken');
    const refreshToken = qp.get('refreshToken');;

    if (!accessToken) {
      this.router.navigate(['/login'], {
        queryParams: { error: 'missing_token' },
        replaceUrl: true,
      });
      return;
    }

    // 1) Exchange tokens for cookies
    this.authService.getCookies({ accessToken, refreshToken })
      .pipe(
        take(1),
        catchError(err => {
          console.error('OAuth2 redirect handling failed', err);
          this.router.navigate(['/login'], {
            queryParams: { error: 'oauth_failed' },
            replaceUrl: true,
          });
          return EMPTY; // stop the chain so we don't navigate to '/'
        })
      )
      .subscribe(() => {
        this.router.navigateByUrl('/', { replaceUrl: true });
      });
  }
}
