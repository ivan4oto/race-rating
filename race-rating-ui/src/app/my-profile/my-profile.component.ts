import {Component, OnInit} from '@angular/core';
import {AuthService} from "../auth/oauth2-redirect-handler/auth.service";
import {UserModel} from "../auth/oauth2-redirect-handler/stored-user.model";

@Component({
  selector: 'app-my-profile',
  standalone: true,
  imports: [],
  templateUrl: './my-profile.component.html',
  styleUrl: './my-profile.component.scss'
})
export class MyProfileComponent implements OnInit{
  public currentUser: UserModel = {
    username: '',
    name: '',
    email: '',
    imageUrl: '',
    role: '',
    votedForRaces: [],
    commentedForRaces: []
  };


  constructor(
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
        this.currentUser = this.authService.getUser();
    }

}
