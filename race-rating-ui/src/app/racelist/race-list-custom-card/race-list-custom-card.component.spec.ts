import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RaceListCustomCardComponent } from './race-list-custom-card.component';

describe('RaceListCustomCardComponent', () => {
  let component: RaceListCustomCardComponent;
  let fixture: ComponentFixture<RaceListCustomCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RaceListCustomCardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RaceListCustomCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
