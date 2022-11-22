import { Component, Input, OnInit } from '@angular/core';
import { PostDto } from 'src/app/model/post-dto';

@Component({
  selector: 'app-post-gallery',
  templateUrl: './post-gallery.component.html',
  styleUrls: ['./post-gallery.component.scss']
})
export class PostGalleryComponent implements OnInit {

  @Input() posts: PostDto[] = [];

  constructor() { }

  ngOnInit(): void {
  }
}
