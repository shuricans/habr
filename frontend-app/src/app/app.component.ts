import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from './service/auth.service';
import { MessageService } from './service/message.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements OnDestroy {
  title = 'frontend-app';
  isModeratorOrAdmin: boolean = false;
  private subscription: Subscription;

  constructor(private authService: AuthService,
              private messageService: MessageService) {
    this.subscription = this.messageService.getMessage().subscribe(
       message => {
        if (message === 'updateSecondHeader') {
          this.update();
        }
    });
    this.update();
  }
  
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private update() {
    this.isModeratorOrAdmin = this.authService.isModeratorOrAdmin();
  }
}
