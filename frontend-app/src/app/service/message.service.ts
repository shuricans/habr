import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  
  private subject = new Subject<string>();

  sendMessage(message: string): void {
    this.subject.next(message);
  }

  getMessage(): Observable<string> {
    return this.subject.asObservable();
  }
}
