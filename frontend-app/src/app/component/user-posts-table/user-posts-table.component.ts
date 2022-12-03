import { Component, OnDestroy, OnInit, TemplateRef } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { Page } from 'src/app/model/page';
import { PageFilter } from 'src/app/model/page-filter';
import { PostDataRequest } from 'src/app/model/post-data-request';
import { PostDto } from 'src/app/model/post-dto';
import { PostFilterOwn } from 'src/app/model/post-filter-own';
import { TopicDto } from 'src/app/model/topic-dto';
import { DataService } from 'src/app/service/data.service';
import { DateFormatService } from 'src/app/service/date-format.service';
import { PostService } from 'src/app/service/post.service';
import { TopicService } from 'src/app/service/topic.service';

@Component({
  selector: 'app-user-posts-table',
  templateUrl: './user-posts-table.component.html',
  styleUrls: ['./user-posts-table.component.scss']
})
export class UserPostsTableComponent implements OnInit, OnDestroy {

  private readonly TAG_REGEXP = '^[A-Za-zА-Яа-я0-9][A-Za-zА-Яа-я0-9_-\\s]{0,18}[A-Za-zА-Яа-я0-9]$';
  readonly maxAmountTags: number = 10;
  tags: string[] = [];
  condition: string = '';
  page!: Page;
  pageFilter!: PageFilter;
  postFilter!: PostFilterOwn;
  loading: boolean = true;
  error: boolean = false;
  private postId: number = -1;
  topics!: Observable<TopicDto[]>;

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

  form = new FormGroup({
    topic: new FormControl('', Validators.required),
    title: new FormControl('', [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(255),
    ]),
    description: new FormControl('', [
      Validators.required,
      Validators.maxLength(2000),
    ]),
    content: new FormControl('', Validators.required),
  })

  tagFormControl = new FormControl('',
    Validators.pattern(this.TAG_REGEXP),
  );

  constructor(private postService: PostService,
              private topicService: TopicService,
              public dateFormatService: DateFormatService,
              private dataService: DataService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.pageFilter = this.dataService.getLkPostPageFilter();
    this.postFilter = this.dataService.getLkPostFilter();
    this.getPage(this.pageFilter.page);

    this.topicService.findAllTopics().subscribe({
      next: topics => {
        this.topics = of(topics);
      },
      error: error => {
        console.log(`Error ${error}`);
      }
    })
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

  openPostEditModal(content: TemplateRef<any>, post: PostDto | null) {
    this.modalService.open(content, { fullscreen: true });

    if (post != null) {
      this.postId = post.id;
    } else {
      this.postId = -1;
    }
    
    this.initForm(post);
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

  initForm(post: PostDto | null) {
    if (post == null) {
      this.condition = this.conditions['DRAFT'];
      this.form.reset();
      this.tagFormControl.reset();
      this.tags.splice(0);
    } else {
      this.form.controls.title.setValue(post.title);
      this.form.controls.topic.setValue(post.topic);
      this.form.controls.description.setValue(post.description);
      this.form.controls.content.setValue(post.content);
      this.tags = post.tags;
      this.condition = this.conditions[post.condition];
    }
  }

  addTag(newTag: string) {
    if (this.tagFormControl.invalid || newTag === '') {
      this.tagFormControl.markAsTouched();
      return;
    }
    if (this.tags.length === this.maxAmountTags) {
      return;
    }
    this.tags.push(newTag);
  }

  removeTag(tag: string) {
    const startIndex = this.tags.indexOf(tag);
    const deleteCount = 1;

    if (startIndex !== -1) {
      this.tags.splice(startIndex, deleteCount);
    }
  }

  selectTopic(name: string) {
    this.form.controls.topic.setValue(name);
  }

  submitForm() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    let postDataRequest = new PostDataRequest();

    if (this.postId !== -1) {
      postDataRequest.postId = this.postId;
    }

    postDataRequest.title = (this.form.get('title')?.value)!;
    postDataRequest.content = (this.form.get('content')?.value)!;
    postDataRequest.description = (this.form.get('description')?.value)!;
    postDataRequest.topic = (this.form.get('topic')?.value)!;
    postDataRequest.tags = this.tags;

    this.postService.save(postDataRequest).subscribe({
      next: postDto => {
        alert(`Post with id = ${postDto.id} was saved successfully!`);
        this.getPage(this.pageFilter.page);
      },
      error: error => {
        alert('Error...');
        console.log(error);
      },
    });
  }
  
  reloadPage() {
    location.reload();
  }
}
