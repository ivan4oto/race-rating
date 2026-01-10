import {Component, EventEmitter, Output} from '@angular/core';
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    FormsModule
  ],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.scss'
})
export class SearchBarComponent {
  @Output() searchTermChange: EventEmitter<string> = new EventEmitter<string>();

  constructor() {
  }
  updateSearchTerm(searchTerm: Event) {
   this.searchTermChange.emit((searchTerm.target as HTMLTextAreaElement).value);
  }
}
