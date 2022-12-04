import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-confirm-modal',
  templateUrl: './confirm-modal.component.html',
  styleUrls: ['./confirm-modal.component.scss']
})
export class ConfirmModalComponent {
  
  @Input() message!: string;
  @Input() message_2!: string;

  constructor(public activeModal: NgbActiveModal) {
  }

  closeModal(answer: string) {
    this.activeModal.close(answer);
  }
}
