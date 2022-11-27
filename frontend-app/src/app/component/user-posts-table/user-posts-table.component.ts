import { Component, OnDestroy, OnInit } from '@angular/core';
import { Page } from 'src/app/model/page';
import { PageFilter } from 'src/app/model/page-filter';
import { PostFilterOwn } from 'src/app/model/post-filter-own';
import { DataService } from 'src/app/service/data.service';
import { DateFormatService } from 'src/app/service/date-format.service';
import { PostService } from 'src/app/service/post.service';

@Component({
  selector: 'app-user-posts-table',
  templateUrl: './user-posts-table.component.html',
  styleUrls: ['./user-posts-table.component.scss']
})
export class UserPostsTableComponent implements OnInit, OnDestroy {

  page!: Page;
  pageFilter!: PageFilter;
  postFilter!: PostFilterOwn;
  loading: boolean = true;
  error: boolean = false;

  conditions : Record<string, string> = {
    DRAFT: 'черновик',
    PUBLISHED: 'обупликован',
    HIDDEN: 'скрыт',
    BANNED: 'заблокирован'
  }

  conditionsInDropDown : Record<string, string> = {
    undefined: 'Статус: все',
    DRAFT: 'черновики',
    PUBLISHED: 'обупликованные',
    HIDDEN: 'скрытые',
    BANNED: 'заблокированные'
  }

  constructor(private postService: PostService,
              public dateFormatService: DateFormatService,
              private dataService: DataService) {
  }

  ngOnInit(): void {
    this.pageFilter = this.dataService.getLkPostPageFilter();
    this.postFilter = this.dataService.getLkPostFilter();
    this.getPage(this.pageFilter.page);
  }

  ngOnDestroy() {
    this.dataService.setLkPostPageFilter(this.pageFilter);
    this.dataService.setLkPostFilter(this.postFilter);
  }

  changeSize(size: number) {
    this.pageFilter.size = size;
    this.getPage(1);
  }

  changeCondition(condition?: string) {
    this.postFilter.condition = condition ? condition : undefined!;
    this.getPage(1);
  }

  getPage(page: number) {
    this.loading = true;
    this.pageFilter.page = page;
    this.dataService.setLkPostPageFilter(this.pageFilter);
    this.dataService.setLkPostFilter(this.postFilter);

    this.postService.findOwnPosts(this.pageFilter, this.postFilter).subscribe({
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
}
