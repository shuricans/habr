import { Component, OnDestroy, OnInit } from '@angular/core';
import { first } from 'rxjs';
import { PageFilter } from 'src/app/model/page-filter';
import { PageOfUsers } from 'src/app/model/PageOfUsers';
import { UserFilter } from 'src/app/model/user-filter';
import { DataService } from 'src/app/service/data.service';
import { DateFormatService } from 'src/app/service/date-format.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-all-users-page',
  templateUrl: './all-users-page.component.html',
  styleUrls: ['./all-users-page.component.scss']
})
export class AllUsersPageComponent implements OnInit, OnDestroy {

  page!: PageOfUsers;
  pageFilter!: PageFilter;
  userFilter!: UserFilter;
  sortField!: string;
  sortDir!: string;
  loading: boolean = false;
  error: boolean = false;

  constructor(private userService: UserService,
              private dataService: DataService,
              public dateFormatService: DateFormatService) {
  }

  ngOnInit(): void {
    this.pageFilter = this.dataService.getUsersPageFilter();
    this.userFilter = this.dataService.getUsersUserFilter();
    this.getPage(this.pageFilter.page);
  }

  ngOnDestroy(): void {
    this.dataService.setUsersPageFilter(this.pageFilter);
    this.dataService.setUsersUserFilter(this.userFilter);
  }

  sortBy(sortField: string) {
    throw new Error('Method not implemented.');
  } 

  getPage(page: number) {
    this.loading = true;
    this.pageFilter.page = page;
    this.dataService.setUsersPageFilter(this.pageFilter);
    this.dataService.setUsersUserFilter(this.userFilter);

    this.userService.getAllUsers(this.pageFilter, this.userFilter).pipe(first()).subscribe({
      next: page => {
        this.page = page;
        this.pageFilter.size = page.size;
      },
      error: err => {
        console.error(`Error loading users ${err}`);
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
}
