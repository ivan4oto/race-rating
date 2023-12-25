import {Component, OnInit} from '@angular/core';
import {Movie, RaceService} from "./race.service";
import {NgForOf} from "@angular/common";
import {RacelistCardComponent} from "./racelist-card/racelist-card.component";
import {RaceListModel} from "./RaceListModel";
import {SearchBarComponent} from "./search-bar/search-bar.component";

@Component({
  selector: 'app-racelist',
  standalone: true,
  imports: [
    NgForOf,
    RacelistCardComponent,
    SearchBarComponent
  ],
  templateUrl: './racelist.component.html',
  styleUrl: './racelist.component.scss'
})
export class RacelistComponent implements OnInit{
  allRaces: RaceListModel[] = [
    {id: 1, name: 'Balkan Ultra',  distance: 78, elevation: 6700, rating: 5, logoUrl: 'https://balkanultra.s3.eu-central-1.amazonaws.com/images/home/why.jpg'},
    {id: 2, name: 'Tryavna Ultra',  distance: 150, elevation: 6684, rating: 4, logoUrl: 'https://i.ytimg.com/vi/BgqGre6bkBg/sddefault.jpg'},
    {id: 3, name: 'Vitosha 100',  distance: 100, elevation: 1600, rating: 5, logoUrl: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTwe3KBFgp51Is6hpwYzt4LvW13jJIQxIHblTkdL5GiaVWBqFFg4zJCTDjqKNRsQvskQOI&usqp=CAU'},
    {id: 4, name: 'Thracian Ultra',  distance: 50, elevation: 2630, rating: 4, logoUrl: 'https://thracian-ultra.com/wordpress/wp-content/uploads/2023/11/TU-Logo.png'},
    {id: 5, name: 'Gela Run',  distance: 21, elevation: 1060, rating: 5, logoUrl: 'https://thracian-ultra.com/wordpress/wp-content/uploads/2023/11/TU-Logo.png'},
    {id: 6, name: 'Pancharevo Trail',  distance: 42, elevation: 1882, rating: 3, logoUrl: 'https://pancharevo.begach.com/wp-content/uploads/2022/07/Pancharevo-Trail-marathon-Logo-Square.png'},
    {id: 7, name: 'Kodzha Kaya',  distance: 42, elevation: 2500, rating: 5, logoUrl: 'https://www.race-tracking.com/i/fb/67.jpg'}
  ];

  constructor(
    private raceService: RaceService
  ) {
  }
  ngOnInit() {
    // this.raceService.fetchData().subscribe({
    //   next: (data) => this.movies = data
    // })
  }
}
