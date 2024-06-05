import {Component, OnInit} from '@angular/core';
import {UserControlComponent} from "./user-control/user-control.component";

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [
    UserControlComponent
  ],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.scss'
})
export class AdminPanelComponent implements OnInit {
  constructor() {
  }
  ngOnInit(): void {
    // throw new Error('Method not implemented.');
  }


}
