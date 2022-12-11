import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllPostsPageComponent } from './all-posts-page.component';

describe('AllPostsPageComponent', () => {
  let component: AllPostsPageComponent;
  let fixture: ComponentFixture<AllPostsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllPostsPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllPostsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
