import { Component } from '@angular/core';
import {AuthService} from "../oauth2-redirect-handler/auth.service";
import {UserModel} from "../oauth2-redirect-handler/stored-user.model";


@Component({
  selector: 'app-user-display',
  standalone: true,
  imports: [],
  templateUrl: './user-display.component.html',
  styleUrl: './user-display.component.scss'
})
export class UserDisplayComponent {
  user: UserModel;
  constructor(private authService: AuthService) {
    this.user = authService.getUser();
  }
}
