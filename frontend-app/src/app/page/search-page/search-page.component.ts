import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, Observable } from 'rxjs';
import { Page } from 'src/app/model/page';
import { PageFilter } from 'src/app/model/page-filter';
import { PostFilter } from 'src/app/model/post-filter';
import { TopicDto } from 'src/app/model/topic-dto';
import { DateFormatService } from 'src/app/service/date-format.service';
import { PostService } from 'src/app/service/post.service';
import { TopicService } from 'src/app/service/topic.service';


@Component({
  selector: 'app-search-page',
  templateUrl: './search-page.component.html',
  styleUrls: ['./search-page.component.scss']
})
export class SearchPageComponent implements OnInit {
  username!: string;
  topic!: string;
  topicInDropDown!: string;
  tag!: string;
  pageNumber!: number;
  size!: number;
  sortField!: string;
  sortDir!: string;

  page!: Page;
  loading: boolean = false;
  pageFilter!: PageFilter;
  postFilter!: PostFilter;

  topics!: Observable<TopicDto[]>;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private postService: PostService,
              public dateFormatService: DateFormatService,
              private topicService: TopicService) {
    router.events.pipe(
      filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.init();
      });

    this.pageFilter = new PageFilter();
    this.postFilter = new PostFilter();
  }

  getTopicLink(topicName: string): string {
    return this.topicService.topicLink[topicName];
  }

  ngOnInit(): void {
    this.topics = this.topicService.findAllTopics();
    this.init();
  }

  init() {
    this.route.queryParams.subscribe((queryParam) => {
      this.username = queryParam['username'];
      this.topic = queryParam['topic'];
      this.topicInDropDown = this.topic ?? 'все разделы';
      this.tag = queryParam['tag'];
      this.pageNumber = queryParam['page'] ?? 1;
      this.size = queryParam['size'];
      this.sortField = queryParam['sortField'] ?? 'created';
      this.sortDir = queryParam['sortDir'] ?? 'DESC';
    });
    
    this.pageFilter.page = this.pageNumber;
    this.pageFilter.size = this.size;
    this.pageFilter.sortField = this.sortField;
    this.pageFilter.sortDir = this.sortDir;

    this.postFilter.tag = this.tag;
    this.postFilter.topic = this.topic;
    this.postFilter.username = this.username;

    this.getPage();
  }

  getPage() {
    this.loading = true;

    this.postService.findAllPublishedPost(this.pageFilter, this.postFilter).subscribe({
      next: page => {
        this.page = page;
        this.pageFilter.size = page.size;
      },
      error: () => {
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
        window.scrollTo(0, 0);
      }
    });
  }

  changeTopic(newTopic?: string) {
    this.router.navigate(
      ['/search'],
      {
        queryParams: { page: undefined, topic: newTopic },
        queryParamsHandling: 'merge'
      }
    ).then(() => location.reload());
  }

  changeSize(newSize: number) {
    this.router.navigate(
      ['/search'],
      {
        queryParams: { page: undefined, size: newSize },
        queryParamsHandling: 'merge'
      }
    ).then(() => location.reload());
  }

  changePage(newPage: number) {
    this.router.navigate(
      ['/search'],
      {
        queryParams: { page: newPage },
        queryParamsHandling: 'merge'
      }
    ).then(() => location.reload());
  }

  sortBy(sortFieldValue: string) {
    if (sortFieldValue === this.sortField) {
      this.sortDir = this.sortDir === 'DESC' ? 'ASC' : 'DESC';
    } else {
      this.sortDir = 'ASC';
    }
    this.router.navigate(
      ['/search'],
      {
        queryParams: { page: undefined, sortField: sortFieldValue, sortDir: this.sortDir },
        queryParamsHandling: 'merge'
      }
    ).then(() => location.reload());
  }

  removeTag() {
    this.router.navigate(
      ['/search'],
      {
        queryParams: { page: undefined, tag: undefined },
        queryParamsHandling: 'merge'
      }
    ).then(() => location.reload());
  }
}
