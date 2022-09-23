import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignPageComponent } from './design-page.component';

describe('DesignPageComponent', () => {
  let component: DesignPageComponent;
  let fixture: ComponentFixture<DesignPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DesignPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DesignPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
