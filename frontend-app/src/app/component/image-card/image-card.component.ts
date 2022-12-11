import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PictureData } from 'src/app/model/picture-data';
import { PictureService } from 'src/app/service/picture.service';
import { Clipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-image-card',
  templateUrl: './image-card.component.html',
  styleUrls: ['./image-card.component.scss']
})
export class ImageCardComponent implements OnInit {

  @Input() primaryId!: number;
  @Input() picture!: PictureData;
  @Output() setMainPicEvent = new EventEmitter<number>();
  @Output() deletePicEvent = new EventEmitter<PictureData>();
  @Output() removeMainPicEvent = new EventEmitter<void>();

  imageToShow: any;
  isImageLoading?: boolean;

  constructor(private pictureService: PictureService,
              private clipboard: Clipboard) {
  }


  ngOnInit(): void {
    this.getImageFromService();
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

    this.pictureService.getPicture(this.picture.id)
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

  setAsPrimary() {
    this.setMainPicEvent.emit(this.picture.id);
  }

  getLink() {
    let link = this.pictureService.getLink(this.picture.id);
    this.clipboard.copy(link);
  }

  delete() {
    this.deletePicEvent.emit(this.picture);
  }

  removePrimary() {
    this.removeMainPicEvent.emit();
  }
}
