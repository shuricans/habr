import { Component, Input, OnInit } from '@angular/core';
import { PostDto } from 'src/app/model/post-dto';
import { DateFormatService } from 'src/app/service/date-format.service';
import { PictureService } from 'src/app/service/picture.service';
import { TopicService } from 'src/app/service/topic.service';

@Component({
  selector: 'app-post-card',
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.scss']
})
export class PostCardComponent implements OnInit {

  @Input() post!: PostDto;
  imageToShow: any;
  isImageLoading!: boolean;

  constructor(public dateFormatService: DateFormatService,
              private pictureService: PictureService,
              private topicService: TopicService) { 
  }

  ngOnInit(): void {
    if (this.post.mainPictureId != null) {
      this.getImageFromService();
    }
  }

  getTopicLink(topic: string): string {
    return this.topicService.topicLink[topic];
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
