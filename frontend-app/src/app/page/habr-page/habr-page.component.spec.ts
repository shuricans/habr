import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HabrPageComponent } from './habr-page.component';

describe('HabrPageComponent', () => {
  let component: HabrPageComponent;
  let fixture: ComponentFixture<HabrPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HabrPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HabrPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
