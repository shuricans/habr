import { Component, OnDestroy, OnInit, TemplateRef } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { first, Observable, of } from 'rxjs';
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
import { ConfirmModalComponent } from '../confirm-modal/confirm-modal.component';
import { InfoModalComponent } from '../info-modal/info-modal.component';

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
  postId: number = -1;
  topics!: Observable<TopicDto[]>;
  private modalEditPostReference!: NgbModalRef;
  otherChanges: boolean = false;

  conditions : Record<string, string> = {
    DRAFT: 'черновик',
    PUBLISHED: 'опубликован',
    HIDDEN: 'скрыт',
    BANNED: 'заблокирован'
  }

  conditionsInDropDown : Record<string, string> = {
    undefined: 'Статус: все',
    DRAFT: 'черновики',
    PUBLISHED: 'опубликованные',
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

    this.topicService.findAllTopics().pipe(first()).subscribe({
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
    this.modalEditPostReference = this.modalService.open(content, { 
      fullscreen: true,
      beforeDismiss: () => {
        if (!(this.form.dirty || this.otherChanges)) { // there were no changes 
          return true; // closing modal
        }
        const modalRef = this.modalService.open(ConfirmModalComponent);
        modalRef.componentInstance.message = 'Вы точно хотите закрыть форму?';
        modalRef.componentInstance.message_2 = 'Все несохранённые изменения будут потеряны.';

        modalRef.result.then(
          () => { // yes event
            this.modalEditPostReference.close();
          }, 
          () => { // catch all close events here
          }
        );
        return false;
      }
    });

    // after closing the modal editing
    this.modalEditPostReference.result.finally(
      () => {
      }
    );

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

    this.postService.findOwnPosts(this.pageFilter, this.postFilter).pipe(first()).subscribe({
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
    this.resetForms();
    // post == null, means we want empty form
    if (post == null) {
      this.condition = this.conditions['DRAFT'];
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

  resetForms() {
    this.otherChanges = false; // reset changes flag
    this.tagFormControl.reset(); // reset input for tags
    this.form.reset(); // reset main form
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
    this.otherChanges = true;
  }

  removeTag(tag: string) {
    const startIndex = this.tags.indexOf(tag);
    const deleteCount = 1;

    if (startIndex !== -1) {
      this.tags.splice(startIndex, deleteCount);
      this.otherChanges = true;
    }
  }

  selectTopic(name: string) {
    this.form.controls.topic.setValue(name);
    this.otherChanges = true;
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

    this.postService.save(postDataRequest).pipe(first()).subscribe({
      next: (postDto) => {
        this.getPage(this.pageFilter.page);
        const modalRef = this.modalService.open(InfoModalComponent);
        this.initForm(postDto);
        this.postId = postDto.id;
        modalRef.componentInstance.message = 'Статья успешно сохранена!';
      },
      error: error => {
        alert('Error...');
        console.log(error);
      },
    });
  }
  
  hide(postId: number) {
    const modalRef = this.modalService.open(ConfirmModalComponent);
    modalRef.componentInstance.message = 'Вы действительно хотите скрыть пост?';

    modalRef.result.then(
      (result) => {
        // yes event
        this.postService.hide(postId).pipe(first()).subscribe({
          next: () => {
            this.condition = this.conditions['HIDDEN'];
            this.getPage(this.pageFilter.page);
          },
          error: error => {
            alert('Error, when hide');
            console.log(error);
          }
        });
      }, 
      () => { // catch all close events here
      }
    );
  }

  publish(postId: number) {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.form.dirty || this.otherChanges) { // there are unsaved changes
      const modalRef = this.modalService.open(InfoModalComponent);
      modalRef.componentInstance.message = 'Кажется у Вас есть не сохранённые изменения.';
      modalRef.componentInstance.message_2 = 'Сначала необходимо сохранить.';
      return;
    }

    const modalRef = this.modalService.open(ConfirmModalComponent);
    modalRef.componentInstance.message = 'Вы действительно хотите опубликовать пост?';

    modalRef.result.then(
      (result) => {
        // yes event
        this.postService.publish(postId).pipe(first()).subscribe({
          next: () => {
            this.condition = this.conditions['PUBLISHED'];
            this.getPage(this.pageFilter.page);
            const modalRef = this.modalService.open(InfoModalComponent);
            modalRef.componentInstance.message = 'Статья успешно опубликована!';
          },
          error: error => {
            alert('Error, when hide');
            console.log(error);
          }
        });
      }, 
      () => { // catch all close events here
      }
    );
  }

  deletePostById(postId: number, postTitle: string) {
    const modalRef = this.modalService.open(ConfirmModalComponent);
    modalRef.componentInstance.message = 'Вы действительно хотите удалить пост?';
    modalRef.componentInstance.message_2 = '\"' + postTitle + '\"';

    modalRef.result.then(
      (result) => {
        // yes event
        this.postService.delete(postId).pipe(first()).subscribe({
          next: () => {
            this.getPage(this.pageFilter.page);
            const modalRef = this.modalService.open(InfoModalComponent);
            modalRef.componentInstance.message = 'Статья успешно удалена!';
          },
          error: error => {
            alert('Error, when hide');
            console.log(error);
          }
        });
      }, 
      () => { // catch all close events here
      }
    );
  }

  reloadPage() {
    location.reload();
  }
}
