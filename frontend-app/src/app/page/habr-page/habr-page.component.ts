import { Component, OnInit } from '@angular/core';
import { Page } from 'src/app/model/page';
import { PageFilter } from 'src/app/model/page-filter';
import { PostService } from 'src/app/service/post.service';

@Component({
  selector: 'app-habr-page',
  templateUrl: './habr-page.component.html',
  styleUrls: ['./habr-page.component.scss']
})
export class HabrPageComponent implements OnInit {

  private readonly LAST_PAGE = 'habr_lastPage';
  private readonly SIZE = 'habr_size';

  loading: boolean = true;
  page!: Page;
  pageFilter?: PageFilter;
  size: number = 5;
  pageNumber: number = 1;
  error: boolean = false;

  constructor(private postService: PostService) {
  }

  ngOnInit(): void {
    if (localStorage.getItem(this.LAST_PAGE)) {
      this.pageNumber = Number(localStorage.getItem(this.LAST_PAGE));
    }
    if (localStorage.getItem(this.SIZE))  {
      this.size = Number(localStorage.getItem(this.SIZE));
    }
    this.getPage(this.pageNumber);
  }

  changeSize(size: number) {
    this.size = size;
    this.getPage(1);
    localStorage.setItem(this.SIZE, String(size));
  }

  reloadPage() {
    location.reload();
  }

  getPage(page: number) {
    localStorage.setItem(this.LAST_PAGE, String(page));
    this.loading = true;
    this.pageFilter = new PageFilter;
    this.pageFilter.page = page;
    this.pageFilter.size = this.size;

    this.postService.findAllPublishedPost(this.pageFilter).subscribe({
      next: page => {
        this.page = page;
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
