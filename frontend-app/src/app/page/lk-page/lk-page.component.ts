import { Component, HostListener, OnInit } from '@angular/core';
import LockableComponent from 'src/app/guard/lockable-component';
import { DataService } from 'src/app/service/data.service';

@Component({
  selector: 'app-lk-page',
  templateUrl: './lk-page.component.html',
  styleUrls: ['./lk-page.component.scss']
})
export class LkPageComponent implements OnInit, LockableComponent {

  activeComponent: number = 1;
  allowRedirect: boolean = true;

  @HostListener('window:beforeunload', ['$event'])
  beforeUnloadHander() {
     return this.allowRedirect;
  }

  constructor(private dataService: DataService) {
  }

  canDeactivate(): boolean {
    return this.allowRedirect;
  }
  
  ngOnInit(): void {
    this.activeComponent = this.dataService.getLkActiveComponent();
  }

  showComponent(active: number) {
    this.activeComponent = active;
    this.dataService.setLkActiveComponent(active);
  }
}
