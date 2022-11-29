import {Component, OnDestroy, OnInit} from '@angular/core';
import {Page} from "../../model/page";
import {PageFilter} from "../../model/page-filter";
import {PostFilter} from "../../model/post-filter";
import {PostService} from "../../service/post.service";
import {DataService} from "../../service/data.service";

@Component({
  selector: 'app-mobile-page',
  templateUrl: './mobile-page.component.html',
  styleUrls: ['./mobile-page.component.scss']
})
export class MobilePageComponent implements OnInit, OnDestroy {
  page!: Page;
  pageFilter!: PageFilter;
  loading: boolean = true;
  error: boolean = false;
  postFilter: PostFilter;
  private readonly MOBILE_TOPIC = 'Мобильная разработка';

  constructor(private postService: PostService,
              private dataService: DataService) {
    this.postFilter = new PostFilter();
    this.postFilter.topic = this.MOBILE_TOPIC;
  }

  ngOnInit(): void {
    this.pageFilter = this.dataService.getMobilePageFilter();
    this.getPage(this.pageFilter.page);
  }

  ngOnDestroy() {
    this.dataService.setMobilePageFilter(this.pageFilter);
  }

  changeSize(size: number) {
    this.pageFilter.size = size;
    this.getPage(1);
  }

  reloadPage() {
    location.reload();
  }

  getPage(page: number) {
    this.loading = true;
    this.pageFilter.page = page;
    this.dataService.setMobilePageFilter(this.pageFilter);

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
}
