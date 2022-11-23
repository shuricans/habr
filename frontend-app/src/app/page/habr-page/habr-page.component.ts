import { Component, OnInit } from '@angular/core';
import { Page } from 'src/app/model/page';
import { PostDto } from 'src/app/model/post-dto';
import { PostService } from 'src/app/service/post.service';

@Component({
  selector: 'app-habr-page',
  templateUrl: './habr-page.component.html',
  styleUrls: ['./habr-page.component.scss']
})
export class HabrPageComponent implements OnInit {

  postsLoaded: boolean = false
  page?: Page
  posts: PostDto[] = []

  constructor(private postService: PostService) {
  }

  ngOnInit(): void {
    console.log('Loading published posts...')
    this.postService.findAllPublishedPost().subscribe({
      next: page => {
        console.log('Posts successfully loaded.')
        this.page = page
        this.posts = page.content
      },
      error: err => {
        console.error(`Error loading posts ${err}`)
      },
      complete: () => {
        this.postsLoaded = true
      }
    })
  }
}
