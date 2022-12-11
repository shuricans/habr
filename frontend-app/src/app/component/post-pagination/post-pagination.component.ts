import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-post-pagination',
  templateUrl: './post-pagination.component.html',
  styleUrls: ['./post-pagination.component.scss']
})
export class PostPaginationComponent {

  @Output() changePageEvent = new EventEmitter<number>();
  @Output() changeSizeEvent = new EventEmitter<number>();
  @Input() loading: boolean = true;
  @Input() size!: number;
  @Input() text: string = 'отображать на странице';

  pageEvent(event: number) {
    this.changePageEvent.emit(event);
  }

  sizeEvent(size: number) {
    this.changeSizeEvent.emit(size);
  }
}
