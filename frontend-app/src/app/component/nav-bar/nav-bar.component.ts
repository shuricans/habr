import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {filter} from "rxjs";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss']
})
export class NavBarComponent implements OnInit {

  isHabrPage: boolean = false;
  isDesignPage: boolean = false;
  isWebDevPage: boolean = false;
  isMobileDevPage: boolean = false;
  isMarketingPage: boolean = false;
  isHelpPage: boolean = false;
  isSearchPage: boolean = false;
  isLoginPage: boolean = false;

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    // this.router.events.pipe(filter(event => event instanceof NavigationEnd))
    //   .subscribe((event: NavigationEnd) => {
    //     this.isHabrPage = event.url === '/' || event.url === '/habr';
    //     this.isDesignPage = event.url === '/design';
    //     this.isWebDevPage = event.url === '/web';
    //     this.isMobileDevPage = event.url === '/mobile';
    //     this.isMarketingPage = event.url === '/marketing';
    //     this.isSearchPage = event.url === '/search';
    //   });
    // this.router.events.subscribe(event => {
    //   if (event instanceof NavigationEnd) {
    //     this.isHabrPage = event.url === '/' || event.url === '/habr';
    //     this.isDesignPage = event.url === '/design';
    //     this.isWebDevPage = event.url === '/web';
    //     this.isMobileDevPage = event.url === '/mobile';
    //     this.isMarketingPage = event.url === '/marketing';
    //     this.isSearchPage = event.url === '/search';
    //   }
    // });
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      console.log(this.activatedRoute.root);
    });
  }

}
