import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RacedetailComponent } from './racedetail.component';

describe('RacedetailComponent', () => {
  let component: RacedetailComponent;
  let fixture: ComponentFixture<RacedetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RacedetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RacedetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
