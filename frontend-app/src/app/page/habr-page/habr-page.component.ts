import { Component, OnDestroy, OnInit } from '@angular/core';
import { Page } from 'src/app/model/page';
import { PageFilter } from 'src/app/model/page-filter';
import { DataService } from 'src/app/service/data.service';
import { PostService } from 'src/app/service/post.service';

@Component({
  selector: 'app-habr-page',
  templateUrl: './habr-page.component.html',
  styleUrls: ['./habr-page.component.scss']
})
export class HabrPageComponent implements OnInit, OnDestroy {

  page!: Page;
  pageFilter!: PageFilter;
  loading: boolean = true;
  error: boolean = false;

  constructor(private postService: PostService,
              private dataService: DataService) {
  }

  ngOnInit(): void {
    this.pageFilter = this.dataService.getHabrPageFilter();
    this.getPage(this.pageFilter.page);
  }

  ngOnDestroy() {
    this.dataService.setHabrPageFilter(this.pageFilter);
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
    this.dataService.setHabrPageFilter(this.pageFilter);

    this.postService.findAllPublishedPost(this.pageFilter).subscribe({
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
