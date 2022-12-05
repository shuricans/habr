import { Component, Input, OnInit } from '@angular/core';
import { PostDto } from 'src/app/model/post-dto';
import { DateFormatService } from 'src/app/service/date-format.service';
import { TopicService } from 'src/app/service/topic.service';

@Component({
  selector: 'app-post-card',
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.scss']
})
export class PostCardComponent implements OnInit {

  private _post?: PostDto;

  constructor(public dateFormatService: DateFormatService,
              private topicService: TopicService) { 
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

  getTopicLink(topic: string): string {
    return this.topicService.topicLink[topic];
  }
}
