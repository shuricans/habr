import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Page } from 'src/app/model/page';
import { PageFilter } from 'src/app/model/page-filter';
import { PostFilter } from 'src/app/model/post-filter';
import { UserDto } from 'src/app/model/user-dto';
import { PostService } from 'src/app/service/post.service';
import { UserService } from 'src/app/service/user.service';
import { ExceptionDetails } from "src/app/model/exception-details";

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.scss']
})
export class UserPageComponent implements OnInit {

  page!: Page;
  pageFilter!: PageFilter;
  loading: boolean = false;
  postFilter?: PostFilter;
  user?: UserDto;
  error: boolean = false;
  userNotExist: boolean = false;
  otherError: boolean = false;

  roles : Record<string, string> = {
    ROLE_USER: 'Пользователь',
    ROLE_MODERATOR: 'Модератор',
    ROLE_ADMIN: 'Администратор'
  }

  constructor(private route: ActivatedRoute,
              private userService: UserService,
              private postService: PostService) {
  }

  ngOnInit(): void {
    const routeParams = this.route.snapshot.paramMap;
    const username = routeParams.get('username')!;

    this.postFilter = new PostFilter();
    this.postFilter.username = username;

    this.pageFilter = new PageFilter();

    this.userService.getActiveUserByUsername(username).subscribe({
      next: userDto => {
        this.user = userDto;
      },
      error: errorObject => {
        let exceptionDetails = errorObject.error as ExceptionDetails;
        if (exceptionDetails.status === 404) {
          this.userNotExist = true;
        } else {
          this.otherError = true;
        }
      }
    });

    this.getPage(1);
  }

  getPage(page: number) {
    this.loading = true;
    this.pageFilter.page = page;

    this.postService.findAllPublishedPost(this.pageFilter, this.postFilter).subscribe({
      next: page => {
        this.page = page;
        this.pageFilter.size = page.size;
      },
      error: err => {
        console.error(`Error loading posts ${err}`);
        this.error = true;
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
        window.scrollTo(0, 0);
      }
    });
  }

  changeSize(size: number) {
    this.pageFilter.size = size;
    this.getPage(1);
  }

  reloadPage() {
    location.reload();
  }
}
