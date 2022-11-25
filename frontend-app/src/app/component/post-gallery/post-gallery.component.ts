import { Component, Input, OnInit } from '@angular/core';
import { Page } from 'src/app/model/page';

@Component({
  selector: 'app-post-gallery',
  templateUrl: './post-gallery.component.html',
  styleUrls: ['./post-gallery.component.scss']
})
export class PostGalleryComponent implements OnInit {

  @Input() page!: Page;

  constructor() { }

  ngOnInit(): void {
  }
}
