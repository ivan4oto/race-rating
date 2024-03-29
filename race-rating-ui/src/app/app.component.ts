import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import {NavbarComponent} from "./navbar/navbar.component";
import {AboutComponent} from "./about/about.component";
import {FooterComponent} from "./footer/footer.component";
import {MatDividerModule} from "@angular/material/divider";

@Component({
  selector: 'app-root',
  standalone: true,
    imports: [CommonModule, RouterOutlet, NavbarComponent, AboutComponent, FooterComponent, MatDividerModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'race-rating-ui';
}
