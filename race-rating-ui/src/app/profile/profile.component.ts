import { Component } from '@angular/core';
import {UserModel} from "../auth/oauth2-redirect-handler/stored-user.model";
import {AuthService} from "../auth/oauth2-redirect-handler/auth.service";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  user: UserModel;
  constructor(private authService: AuthService) {
    this.user = authService.getUser();
  }

}
