import { Component } from '@angular/core';


export interface TestUser {
  name: string;
  avatarUrl: string;
}
@Component({
  selector: 'app-user-display',
  standalone: true,
  imports: [],
  templateUrl: './user-display.component.html',
  styleUrl: './user-display.component.scss'
})
export class UserDisplayComponent {
  user: TestUser = {
    name: 'Ivan Gochev',
    avatarUrl: 'https://lh3.googleusercontent.com/a/AATXAJynIOfHLyXgk7y9v_GWsXkFeHV0tHGMbzXsACwx=s96-c64'
  }
}
