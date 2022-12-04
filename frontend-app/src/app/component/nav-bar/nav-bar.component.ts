import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {filter, map} from "rxjs";
import { AuthService } from 'src/app/service/auth.service';

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
  isLkPage: boolean = false;

  constructor(private router: Router, 
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map(e => e.url)
    ).subscribe(url => {
      this.activateNavLink(url);
    });
  }

  public isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  public logout() {
    this.authService.logout().subscribe({
      next: success => {
        if (success) {
          this.router.navigate(['/login']);
        }
      }
    });
  }

  private activateNavLink(url: string) {
    this.isHabrPage = url === '/habr' || url === '/';
    this.isDesignPage = url === '/design';
    this.isWebDevPage = url === '/web';
    this.isMobileDevPage = url === '/mobile';
    this.isMarketingPage = url === '/marketing';
    this.isHelpPage = url === '/help';
    this.isSearchPage = url.startsWith('/search');
    this.isLoginPage = url === '/login';
    this.isLkPage = url === '/lk';
  }

  getUsername() {
    return this.authService.user?.username;
  }
}
