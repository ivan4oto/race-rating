// can-edit-race.guard.ts
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { map, take } from 'rxjs/operators';
import { AuthService } from '../oauth2-redirect-handler/auth.service';

export const canEditRaceGuard = (route: ActivatedRouteSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const raceId = route.paramMap.get('id');

  return auth.user$.pipe(
    take(1),
    map(user => {
      if (!user) return router.createUrlTree(['/login'], { queryParams: { redirectUrl: router.url } });
      return auth.canEditRace(user, raceId) ? true : router.createUrlTree(['/']);
    })
  );
};
