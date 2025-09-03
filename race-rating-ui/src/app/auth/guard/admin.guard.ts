// admin.guard.ts
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { map, take } from 'rxjs/operators';
import { AuthService } from '../oauth2-redirect-handler/auth.service';

export const adminGuard = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.user$.pipe(
    take(1),
    map(user => user?.role === 'ADMIN' ? true : router.createUrlTree(['/']))
  );
};
