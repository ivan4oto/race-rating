import {Component, OnInit} from '@angular/core';
import {AdminPanelService} from "../admin-panel.service";
import {MatTableModule} from "@angular/material/table";
import {UserModel} from "../../auth/oauth2-redirect-handler/stored-user.model";
import {FormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-user-control',
  standalone: true,
  imports: [
    MatTableModule,
    FormsModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './user-control.component.html',
  styleUrl: './user-control.component.scss'
})
export class UserControlComponent implements OnInit{
  displayedColumns: string[] = ['email', 'username', 'role', 'save'];
  users: UserModel[] = [];
  constructor(private adminPanelService: AdminPanelService) {
  }
  ngOnInit(): void {
    this.adminPanelService.fetchAllUsers().subscribe(users => {
      console.log(users);
      this.users = users;
    });
  }

  saveRow(rowData: any) {
    console.log('saveROW')
    this.adminPanelService.updateUser(rowData).subscribe(user => {
      console.log('User updated', user);
      this.users = this.users.map(u => {
        if(u.email === user.email) {
          return user;
        }
        return u;
      });
    });
  }
}
