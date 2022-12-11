import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, TemplateRef } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { first, Observable, Subscription } from 'rxjs';
import { Page } from 'src/app/model/page';
import { PageFilter } from 'src/app/model/page-filter';
import { PictureData } from 'src/app/model/picture-data';
import { PostDataRequest } from 'src/app/model/post-data-request';
import { PostDto } from 'src/app/model/post-dto';
import { PostFilterOwn } from 'src/app/model/post-filter-own';
import { TopicDto } from 'src/app/model/topic-dto';
import { DataService } from 'src/app/service/data.service';
import { DateFormatService } from 'src/app/service/date-format.service';
import { PictureService } from 'src/app/service/picture.service';
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

  @Input() allowRedirect: boolean = true;
  @Output() allowRedirectChange = new EventEmitter<boolean>();

  // variables for table of posts
  page!: Page;
  pageFilter!: PageFilter;
  postFilter!: PostFilterOwn;
  loading: boolean = true;
  error: boolean = false;

  // variables for editing post in modal dialog
  private readonly TAG_REGEXP = '^[A-Za-zА-Яа-я0-9][A-Za-zА-Яа-я0-9_-\\s]{0,18}[A-Za-zА-Яа-я0-9]$';
  readonly maxAmountTags: number = 10;
  tags: string[] = []; // tags
  condition: string = ''; // for displaing state in header
  private postId: number = -1; // post id ([-1] means it's a draft)
  topics!: Observable<TopicDto[]>; // topics
  dataChanged: boolean = false; // flag for handling changes outside form-control
  private images:string [] = []; // image files after choose-dialog
  pictures: PictureData[] = []; // pictures dto
  mainPicId: number = -1; // id header picture of Post
  private subscriptionOnFormChanges?: Subscription;
  private modalEditPostRef?: NgbModalRef;

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
      Validators.pattern(/[\S]/),
      Validators.minLength(5),
      Validators.maxLength(255),
    ]),
    description: new FormControl('', [
      Validators.required,
      Validators.pattern(/[\S]/),
      Validators.maxLength(2000),
    ]),
    content: new FormControl('', [
      Validators.required,
      Validators.pattern(/[\S]/)
    ]),
  })

  tagFormControl = new FormControl('',
    Validators.pattern(this.TAG_REGEXP),
  );

  inputImageControl = new FormControl(null,
    Validators.required
  );

  constructor(private postService: PostService,
              private topicService: TopicService,
              private dataService: DataService,
              private pictureService: PictureService,
              private modalService: NgbModal,
              public dateFormatService: DateFormatService) {
  }

  ngOnInit(): void {
    this.pageFilter = this.dataService.getLkPostPageFilter();
    this.postFilter = this.dataService.getLkPostFilter();
    this.topics = this.topicService.findAllTopics();
    this.getPage(this.pageFilter.page);
  }

  ngOnDestroy() {
    this.dataService.setLkPostPageFilter(this.pageFilter);
    this.dataService.setLkPostFilter(this.postFilter);
    this.modalEditPostRef?.close();
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
    this.modalEditPostRef = this.modalService.open(content, {
      fullscreen: true,
      beforeDismiss: () => {
        if (!this.dataChanged) { // there were no changes
          return true; // means closing main fullscreen modal
        }
        // opening a confirm dialog
        const modalRef = this.modalService.open(ConfirmModalComponent);
        modalRef.componentInstance.message = 'Вы точно хотите закрыть форму?';
        modalRef.componentInstance.message_2 = 'Все несохранённые изменения будут потеряны.';

        modalRef.result.then(
          () => { // yes event
            this.modalEditPostRef!.close(); // manually closing main fullscreen modal
          },
          () => {} // catch all close events here
        );
        return false; // prevent close
      }
    });

    // set postId ([-1] means it's a draft)
    this.postId = post != null ? post.id : -1;

    this.initForm(post);
    this.subscribeTo_FormChanges();

    // when modal is completely closed
    this.modalEditPostRef.result.finally(() => {
      this.unsubscribeFrom_FormChanges();
      this.resetForm();
      this.resetChangeFlag();
    });
  }

  private handleChangeEvent() {
    if (this.dataChanged === false) {
      this.allowRedirect = false;
      this.allowRedirectChange.emit(this.allowRedirect);
      this.dataChanged = true;
    }
  }

  private resetChangeFlag() {
    this.dataChanged = false; // reset changes flag
    this.allowRedirect = true;
    this.allowRedirectChange.emit(this.allowRedirect);
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

  private initForm(post: PostDto | null) {
    // post == null, means we want empty form
    if (post == null) {
      this.condition = this.conditions['DRAFT'];
    } else {
      this.form.controls.title.setValue(post.title);
      this.form.controls.topic.setValue(post.topic);
      this.form.controls.description.setValue(post.description);
      this.form.controls.content.setValue(post.content);
      this.tags = post.tags;
      this.pictures.push(...post.pictures);
      this.mainPicId = post.mainPictureId ?? -1;
      this.condition = this.conditions[post.condition];
    }
  }

  private subscribeTo_FormChanges() {
    // subscribing to first change event in form
    this.subscriptionOnFormChanges = this.form.valueChanges.pipe(first()).subscribe({
      next: () => {
        this.handleChangeEvent();
      }
    });
  }

  private unsubscribeFrom_FormChanges() {
    this.subscriptionOnFormChanges?.unsubscribe();
  }

  private resetForm() {
    this.tagFormControl.reset(); // reset input FormControl for tags
    this.inputImageControl.reset(); // reset input FormControl for images files
    this.form.reset(); // reset main FormGroup
    this.pictures.splice(0); // clear pictures array
    this.tags.splice(0); // clear tags array
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
    this.tagFormControl.reset(); // reset input FormControl for tags
    this.handleChangeEvent();
  }

  removeTag(tag: string) {
    const startIndex = this.tags.indexOf(tag);
    const deleteCount = 1;

    if (startIndex !== -1) {
      this.tags.splice(startIndex, deleteCount);
      this.handleChangeEvent();
    }
  }

  selectTopic(name: string) {
    this.form.controls.topic.setValue(name);
    this.handleChangeEvent();
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

    if (this.mainPicId !== -1) {
      postDataRequest.mainPictureId = this.mainPicId;
    }

    if (this.pictures.length > 0) {
      postDataRequest.picturesIds = this.pictures.map(pic => pic.id);
    }

    postDataRequest.title = this.form.get('title')?.value!;
    postDataRequest.content = this.form.get('content')?.value!;
    postDataRequest.description = this.form.get('description')?.value!;
    postDataRequest.topic = this.form.get('topic')?.value!;
    postDataRequest.tags = this.tags;

    this.postService.save(postDataRequest).pipe(first()).subscribe({
      next: (postDto) => {
        this.getPage(this.pageFilter.page); // background refresh table of posts
        this.postId = postDto.id; // needed when post didn't exist yet
        this.resetChangeFlag();
        this.unsubscribeFrom_FormChanges();
        this.subscribeTo_FormChanges();
        const modalRef = this.modalService.open(InfoModalComponent);
        modalRef.componentInstance.message = 'Статья успешно сохранена!';
      },
      error: error => {
        alert('Error...');
        console.log(error);
      },
    });
  }

  hide() {
    const modalRef = this.modalService.open(ConfirmModalComponent);
    modalRef.componentInstance.message = 'Вы действительно хотите скрыть пост?';

    modalRef.result.then(
      (result) => {
        // yes event
        this.postService.hide(this.postId).pipe(first()).subscribe({
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

  publish() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.dataChanged) { // there are unsaved changes
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
        this.postService.publish(this.postId).pipe(first()).subscribe({
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

  getFileDetails(event: any) {
    this.images.splice(0);
    for (var i = 0; i < event.target.files.length; i++) {
      this.images.push(event.target.files[i]);
    }
  }

  uploadFiles() {
    if (this.inputImageControl.invalid) {
      return;
    }

    const formData = new FormData();

    for (var i = 0; i < this.images.length; i++) {
      formData.append("pictures", this.images[i]);
    }

    this.pictureService.uploadPictures(formData).pipe(first()).subscribe({
      next: pics => {
        this.pictures.push(...pics);
        this.handleChangeEvent();
        this.inputImageControl.reset();
      },
      error: () => {
        this.inputImageControl.reset();
      }
    });
  }

  setMainPicture(picId: number) {
    this.mainPicId = picId;
    this.handleChangeEvent();
  }

  removeMainPicture() {
    this.mainPicId = -1;
    this.handleChangeEvent();
  }

  deletePicture(picture: PictureData) {
    let message_1 = 'Вы точно хотите удалить эту картинку?';
    let message_2 = 'Убедитесь что Вы не ссылаетесь на неё в статье.';
    if (picture.id === this.mainPicId) {
      message_1 = 'Вы точно хотите удалить главную картинку?';
      message_2 = 'Превью поста будет содержать только описание.';
    }

    const modalRef = this.modalService.open(ConfirmModalComponent);
    modalRef.componentInstance.message = message_1;
    modalRef.componentInstance.message_2 = message_2;

    modalRef.result.then(
      () => {
        // yes event
        const startIndex = this.pictures.indexOf(picture);
        const deleteCount = 1;

        if (startIndex !== -1) {
          this.pictures.splice(startIndex, deleteCount);
          this.handleChangeEvent();
        }

        if (picture.id === this.mainPicId) {
          this.mainPicId = -1;
        }
      },
      () => {} // catch all close events here
    );
  }

  reloadPage() {
    location.reload();
  }
}
