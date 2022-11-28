import {Component, OnInit} from '@angular/core';
import {TopicDto} from "../../model/topic-dto";
import {TopicService} from "../../service/topic.service";
import {Observable, of} from "rxjs";

@Component({
  selector: 'app-post-edit',
  templateUrl: './post-edit.component.html',
  styleUrls: ['./post-edit.component.scss']
})
export class PostEditComponent implements OnInit {

  topics?: Observable<TopicDto[]>;
  topicName!: string;

  constructor(private topicService: TopicService) {
  }

  ngOnInit(): void {
    this.topicService.findAllTopics().subscribe({
      next: topics => {
        this.topics = of(topics);
      },
      error: error => {
        console.log(`Error ${error}`);
      },
      complete: () => {

      }
    })
  }

  selectTopic(name: string) {
    this.topicName = name;
  }
}
