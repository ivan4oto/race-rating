import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import {AuthService} from "../oauth2-redirect-handler/auth.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot
  ): boolean {
    const user = this.authService.getUser();
    if (!user) {
      this.router.navigate(['/login']); // Redirect to login if not authenticated
      return false;
    }

    const raceId = route.paramMap.get('id');
    // Implement logic to fetch race details based on raceId
    // and check if user.id === race.authorId || user.role === 'ADMIN'

    // Assuming a method in authService to check user's permission for a specific race
    if (!this.authService.canEditRace(user, raceId)) {
      this.router.navigate(['/']); // Redirect or show an error message
      return false;
    }

    return true;
  }
}
