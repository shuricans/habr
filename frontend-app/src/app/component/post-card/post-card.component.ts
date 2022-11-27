import { Component, Input, OnInit } from '@angular/core';
import { PostDto } from 'src/app/model/post-dto';
import { DateFormatService } from 'src/app/service/date-format.service';

@Component({
  selector: 'app-post-card',
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.scss']
})
export class PostCardComponent implements OnInit {

  private _post?: PostDto;

  constructor(public dateFormatService: DateFormatService) { 
  }

  ngOnInit(): void {
  }

  @Input()
  set post(value: PostDto | undefined) {
    this._post = value;
  }

  get post(): PostDto | undefined {
    return this._post;
  }
}
