import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPostsTableComponent } from './user-posts-table.component';

describe('UserPostsTableComponent', () => {
  let component: UserPostsTableComponent;
  let fixture: ComponentFixture<UserPostsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserPostsTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserPostsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
