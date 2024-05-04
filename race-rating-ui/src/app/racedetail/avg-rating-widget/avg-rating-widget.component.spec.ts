import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvgRatingWidgetComponent } from './avg-rating-widget.component';

describe('AvgRatingWidgetComponent', () => {
  let component: AvgRatingWidgetComponent;
  let fixture: ComponentFixture<AvgRatingWidgetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvgRatingWidgetComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AvgRatingWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
