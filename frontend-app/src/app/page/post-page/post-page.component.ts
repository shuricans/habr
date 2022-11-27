import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PostDto } from 'src/app/model/post-dto';
import { DateFormatService } from 'src/app/service/date-format.service';
import { PostService } from 'src/app/service/post.service';

@Component({
  selector: 'app-post-page',
  templateUrl: './post-page.component.html',
  styleUrls: ['./post-page.component.scss']
})
export class PostPageComponent implements OnInit {

  post!: PostDto
  notFound!: boolean

  constructor(private postService: PostService,
              private route: ActivatedRoute,
              public dateFormatService: DateFormatService) {
  }

  ngOnInit(): void {
    const routeParams = this.route.snapshot.paramMap
    const postId = Number(routeParams.get('postId'))

    this.postService.findPublishedById(postId).subscribe({
      next: postDto => {
        console.log(`Post with id = ${postId} was successfully loaded.`)
        this.notFound = false;
        this.post = postDto
      },
      error: () => {
        console.error(`Post with id = ${postId} not exist.`)
        this.notFound = true;
      }
    })
  }
}
