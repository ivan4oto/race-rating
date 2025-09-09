// auth.guard.ts
import { inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { map, take } from 'rxjs/operators';
import { AuthService } from '../oauth2-redirect-handler/auth.service';

export const mustBeLoggedInGuard = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.isLoggedIn$.pipe(
    take(1),
    map(isIn => isIn ? true : router.createUrlTree(['/login'], {
      queryParams: { redirectUrl: router.url }
    }))
  );
};
