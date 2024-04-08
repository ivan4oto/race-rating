import {Component, OnInit} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {S3objectModel} from "../misc-models/s3object.model";
import {S3Service} from "../help-services/s3.service";
import {EMPTY, switchMap} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {NgForOf, NgIf} from "@angular/common";
import {NgxFlickingModule, NgxFlickingPanel, Plugin} from "@egjs/ngx-flicking";
import {Arrow} from "@egjs/flicking-plugins";

@Component({
  selector: 'app-carousel',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    NgIf,
    NgForOf,
    NgxFlickingPanel,
    NgxFlickingModule
  ],
  templateUrl: './carousel.component.html',
  styleUrls: [
    './carousel.component.scss',
    '../../../node_modules/@egjs/flicking-plugins/dist/arrow.css'
  ]
})
export class CarouselComponent implements OnInit{
  id: string | null = null;
  s3ImageObjects: S3objectModel[] = [];
  public plugins: Plugin[] = [new Arrow()];

  constructor(
    private s3Service: S3Service,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        this.id = params.get('id');
        return this.id ? this.s3Service.listImages(this.id) : EMPTY;
      })
    ).subscribe(
      {
        next: value => {
          this.s3ImageObjects = value
        },
        error: err => console.log(err)
      }
    )
  }
}
