import { Component, OnInit } from '@angular/core';
import { DataService } from 'src/app/service/data.service';

@Component({
  selector: 'app-lk-page',
  templateUrl: './lk-page.component.html',
  styleUrls: ['./lk-page.component.scss']
})
export class LkPageComponent implements OnInit {

  activeComponent: number = 1;

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.activeComponent = this.dataService.getLkActiveComponent();
  }

  showComponent(active: number) {
    this.activeComponent = active;
    this.dataService.setLkActiveComponent(active);
  }
}
