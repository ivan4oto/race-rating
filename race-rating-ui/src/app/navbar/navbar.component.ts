import {Component} from '@angular/core';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {UserDisplayComponent} from "../auth/user-display/user-display.component";
import {Router, RouterLink} from "@angular/router";
import {AuthService} from "../auth/oauth2-redirect-handler/auth.service";
import {NgIf} from "@angular/common";
import {MatSidenavModule} from "@angular/material/sidenav";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    UserDisplayComponent,
    RouterLink,
    NgIf,
    MatSidenavModule
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  public isExpanded = false;
  constructor(public authService: AuthService, public router: Router) {
  }

  toggleMenu() {
    this.isExpanded = !this.isExpanded;
  }

  logout() {
    this.authService.userLogout();
    this.router.navigate(['/']);
  }

  isAuthenticated() {
    return this.authService.isAuthenticated();
  }
}
