import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ExceptionDetails } from 'src/app/model/exception-details';
import { PostDto } from 'src/app/model/post-dto';
import { DateFormatService } from 'src/app/service/date-format.service';
import { PictureService } from 'src/app/service/picture.service';
import { PostService } from 'src/app/service/post.service';
import { TopicService } from 'src/app/service/topic.service';

@Component({
  selector: 'app-post-page',
  templateUrl: './post-page.component.html',
  styleUrls: ['./post-page.component.scss']
})
export class PostPageComponent implements OnInit {
  post!: PostDto
  notFound: boolean = false;
  otherError: boolean = false;
  httpErrorResponse!: HttpErrorResponse;
  imageToShow: any;
  isImageLoading!: boolean;

  constructor(private postService: PostService,
              private pictureService: PictureService,
              private topicService: TopicService,
              private route: ActivatedRoute,
              public dateFormatService: DateFormatService) {
  }

  ngOnInit(): void {
    const routeParams = this.route.snapshot.paramMap
    const postId = Number(routeParams.get('postId'))

    this.postService.findPublishedById(postId).subscribe({
      next: postDto => {
        this.post = postDto;
        if (this.post.mainPictureId != null) {
          this.getImageFromService();
        }
        window.scrollTo(0, 0);
      },
      error: (httpErrorResponse: HttpErrorResponse) => {
        let exceptionDetails = httpErrorResponse.error as ExceptionDetails;
        if (exceptionDetails.status === 404) {
          this.notFound = true;
          return;
        }
        this.otherError = true;
        this.httpErrorResponse = httpErrorResponse;
        window.scrollTo(0, 0);
      }
    })
  }

  getTopicLink(topic: string): string {
    return this.topicService.topicLink[topic];
  }

  reloadPage() {
    location.reload();
  }

  private createImageFromBlob(image: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      this.imageToShow = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  private getImageFromService() {
    this.isImageLoading = true;

    this.pictureService.getPicture(this.post.mainPictureId)
      .subscribe({
        next: (data) => {
          this.createImageFromBlob(data);
          this.isImageLoading = false;
        },
        error: err => {
          this.isImageLoading = false;
          console.error(err);
        }
      });
  }
}
