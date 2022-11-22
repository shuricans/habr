import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostEditComponentComponent } from './post-edit-component.component';

describe('PostEditComponentComponent', () => {
  let component: PostEditComponentComponent;
  let fixture: ComponentFixture<PostEditComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PostEditComponentComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostEditComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
