import {Component, inject, OnInit} from '@angular/core';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {UserDisplayComponent} from "../auth/user-display/user-display.component";
import {RouterLink} from "@angular/router";
import {AuthService} from "../auth/oauth2-redirect-handler/auth.service";
import {AsyncPipe, NgIf} from "@angular/common";
import {MatSidenavModule} from "@angular/material/sidenav";
import {UserModel} from "../auth/oauth2-redirect-handler/stored-user.model";
import {MatBadgeModule} from "@angular/material/badge";
import {NotificationStore} from "../notifications/NotificationStore";

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
    MatSidenavModule,
    MatBadgeModule,
    AsyncPipe
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent{
  private readonly auth = inject(AuthService);
  private readonly notifications = inject(NotificationStore);

  readonly isLoggedIn$ = this.auth.isLoggedIn$;
  readonly user$ = this.auth.user$;
  readonly unreadCount$ = this.notifications.unreadCount$;

  logout() {
    this.auth.userLogout();
  }
}
