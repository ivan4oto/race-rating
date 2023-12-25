import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RacelistCardComponent } from './racelist-card.component';

describe('RacelistCardComponent', () => {
  let component: RacelistCardComponent;
  let fixture: ComponentFixture<RacelistCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RacelistCardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RacelistCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
