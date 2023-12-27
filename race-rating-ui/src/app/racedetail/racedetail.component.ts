import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-racedetail',
  standalone: true,
  imports: [],
  templateUrl: './racedetail.component.html',
  styleUrl: './racedetail.component.scss'
})
export class RacedetailComponent implements OnInit{
  id: string | null = null;
  constructor(private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.id = params.get('id');
    });
  }

}
