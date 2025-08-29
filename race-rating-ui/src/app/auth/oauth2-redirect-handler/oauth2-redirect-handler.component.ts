import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { switchMap, catchError, of } from 'rxjs';

import { AuthService } from './auth.service';
import { UserModel } from './stored-user.model';

@Component({
  selector: 'app-oauth2-redirect-handler',
  standalone: true,
  imports: [],
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
    const refreshToken = qp.get('refreshToken');
    const redirectUrl = qp.get('redirectUrl') || '/';

    if (!accessToken) {
      // No token -> bounce to login
      this.router.navigate(['/login'], {
        queryParams: { error: 'missing_token' },
        replaceUrl: true,
      });
      return;
    }

    // 1) Exchange tokens for cookies
    this.authService.getCookies({ accessToken, refreshToken })
      .pipe(
        // 2) Optionally record expiry from this response (header name is case-insensitive)
        switchMap((resp: HttpResponse<UserModel>) => {
          const expiresAt = resp.headers.get('Access-Token-Expires-At') || resp.headers.get('access-token-expires-at');
          if (resp.body) {
            // Don’t set isLoggedIn$ here; let storeUserInformation do the full hydration
            this.authService.storeUserModel(resp.body);
          }
          if (expiresAt) {
            this.authService.storeAccessTokenExpiration(expiresAt);
          }
          // 3) Hydrate fully (headers + user) via /api/users/me (sets reactive state, auto-logout)
          return this.authService.storeUserInformation();
        }),
        catchError(err => {
          console.error('OAuth2 redirect handling failed', err);
          // Go to login with an error note
          this.router.navigate(['/login'], {
            queryParams: { error: 'oauth_failed' },
            replaceUrl: true,
          });
          return of(null);
        })
      )
      .subscribe(() => {
        // 4) Done: navigate to final destination and wipe query params from URL
        this.router.navigateByUrl(redirectUrl, { replaceUrl: true });
      });
  }
}
