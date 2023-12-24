import { Component } from '@angular/core';
import {RacelistComponent} from "../racelist/racelist.component";
import {NavbarComponent} from "../navbar/navbar.component";
import {SignupComponent} from "../auth/signup/signup.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RacelistComponent,
    NavbarComponent,
    SignupComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
