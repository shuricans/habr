import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-info-modal',
  templateUrl: './info-modal.component.html',
  styleUrls: ['./info-modal.component.scss']
})
export class InfoModalComponent {
  
  @Input() message!: string;
  @Input() message_2!: string;

  constructor(public activeModal: NgbActiveModal) {
  }

  closeModal(answer: string) {
    this.activeModal.close(answer);
  }
}
