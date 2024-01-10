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
    const token = this.route.snapshot.queryParamMap.get('token');
    if (token) {
      this.authService.handleLogin(token);
      this.authService.storeUserInformation();
      this.router.navigate(['/']); // Redirect to home
    } else {
      this.router.navigate(['/login']); // Redirect to login
    }
  }

}
