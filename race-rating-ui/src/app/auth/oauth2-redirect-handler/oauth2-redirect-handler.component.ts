import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

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
  ) {
  }
  ngOnInit() {
      this.router.navigate(['/']); // Redirect to home
  }

}
