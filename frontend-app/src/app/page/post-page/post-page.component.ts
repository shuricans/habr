import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ExceptionDetails } from 'src/app/model/exception-details';
import { PostDto } from 'src/app/model/post-dto';
import { DateFormatService } from 'src/app/service/date-format.service';
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

  constructor(private postService: PostService,
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
      },
      error: (httpErrorResponse: HttpErrorResponse) => {
        let exceptionDetails = httpErrorResponse.error as ExceptionDetails;
        if (exceptionDetails.status === 404) {
          this.notFound = true;
          return;
        }
        this.otherError = true;
        this.httpErrorResponse = httpErrorResponse;
      }
    })
  }

  getTopicLink(topic: string): string {
    return this.topicService.topicLink[topic];
  }

  reloadPage() {
    location.reload();
  } 
}
