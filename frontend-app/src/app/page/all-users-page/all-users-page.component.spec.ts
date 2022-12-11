import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllUsersPageComponent } from './all-users-page.component';

describe('AllUsersPageComponent', () => {
  let component: AllUsersPageComponent;
  let fixture: ComponentFixture<AllUsersPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllUsersPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllUsersPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
