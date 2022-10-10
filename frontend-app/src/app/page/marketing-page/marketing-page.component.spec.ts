import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MarketingPageComponent } from './marketing-page.component';

describe('MarketingPageComponent', () => {
  let component: MarketingPageComponent;
  let fixture: ComponentFixture<MarketingPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MarketingPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MarketingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
