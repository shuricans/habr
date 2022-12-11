import { Injectable } from '@angular/core';
import { PageFilter } from '../model/page-filter';
import { PostFilterOwn } from '../model/post-filter-own';
import { UserFilter } from '../model/user-filter';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private readonly HABR_PAGE_FILTER = 'habr_page_filter';
  private readonly LK_POST_PAGE_FILTER = 'lk_posts_page_filter';
  private readonly LK_ACTIVE_COMPONENT = 'lk_active_component';
  private readonly LK_POST_FILTER = 'lk_posts_filter';
  private readonly DESIGN_PAGE_FILTER = 'design_page_filter';
  private readonly WEB_PAGE_FILTER = 'web_page_filter';
  private readonly MOBILE_PAGE_FILTER = 'mobile_page_filter';
  private readonly MARKETING_PAGE_FILTER = 'marketing_page_filter';
  private readonly USERS_PAGE_FILTER = 'users_page_filter';
  private readonly USERS_USER_FILTER = 'users_user_filter';

  private habrPageFilter!: PageFilter;
  private lkPostPageFilter!: PageFilter;
  private lkActiveComponent!: number;
  private lkPostFilter!: PostFilterOwn;
  private designPageFilter!: PageFilter;
  private webPageFilter!: PageFilter;
  private mobilePageFilter!: PageFilter;
  private marketingPageFilter!: PageFilter;
  private usersPageFilter!: PageFilter;
  private usersUserFilter!: UserFilter;

  public setHabrPageFilter(pagefilter: PageFilter) {
    this.habrPageFilter = pagefilter;
    localStorage.setItem(this.HABR_PAGE_FILTER, JSON.stringify(this.habrPageFilter));
  }

  public getHabrPageFilter(): PageFilter {
    if (localStorage.getItem(this.HABR_PAGE_FILTER)) {
      this.habrPageFilter = JSON.parse(localStorage.getItem(this.HABR_PAGE_FILTER)!);
    } else {
      this.habrPageFilter = new PageFilter();
    }
    return this.habrPageFilter;
  }

  public setLkPostPageFilter(pagefilter: PageFilter) {
    this.lkPostPageFilter = pagefilter;
    localStorage.setItem(this.LK_POST_PAGE_FILTER, JSON.stringify(this.lkPostPageFilter));
  }

  public getLkPostPageFilter(): PageFilter {
    if (localStorage.getItem(this.LK_POST_PAGE_FILTER)) {
      this.lkPostPageFilter = JSON.parse(localStorage.getItem(this.LK_POST_PAGE_FILTER)!);
    } else {
      this.lkPostPageFilter = new PageFilter();
    }
    return this.lkPostPageFilter;
  }

  public setLkActiveComponent(active: number) {
    this.lkActiveComponent = active;
    localStorage.setItem(this.LK_ACTIVE_COMPONENT, JSON.stringify(this.lkActiveComponent));
  }

  public getLkActiveComponent(): number {
    if (localStorage.getItem(this.LK_ACTIVE_COMPONENT)) {
      this.lkActiveComponent = JSON.parse(localStorage.getItem(this.LK_ACTIVE_COMPONENT)!);
    } else {
      this.lkActiveComponent = 1;
    }
    return this.lkActiveComponent;
  }

  public setLkPostFilter(postFilter: PostFilterOwn) {
    this.lkPostFilter = postFilter;
    localStorage.setItem(this.LK_POST_FILTER, JSON.stringify(this.lkPostFilter));
  }

  public getLkPostFilter(): PostFilterOwn {
    if (localStorage.getItem(this.LK_POST_FILTER)) {
      this.lkPostFilter = JSON.parse(localStorage.getItem(this.LK_POST_FILTER)!);
    } else {
      this.lkPostFilter = new PostFilterOwn();
    }
    return this.lkPostFilter;
  }

  public clearAllData() {
    localStorage.removeItem(this.HABR_PAGE_FILTER);
    localStorage.removeItem(this.LK_POST_PAGE_FILTER);
    localStorage.removeItem(this.LK_ACTIVE_COMPONENT);
    localStorage.removeItem(this.LK_POST_FILTER);
    localStorage.removeItem(this.DESIGN_PAGE_FILTER);
    localStorage.removeItem(this.WEB_PAGE_FILTER);
    localStorage.removeItem(this.MOBILE_PAGE_FILTER);
    localStorage.removeItem(this.MARKETING_PAGE_FILTER);
    localStorage.removeItem(this.USERS_PAGE_FILTER);
    localStorage.removeItem(this.USERS_USER_FILTER);
  }

  getDesignPageFilter(): PageFilter {
    if (localStorage.getItem(this.DESIGN_PAGE_FILTER)) {
      this.designPageFilter = JSON.parse(localStorage.getItem(this.DESIGN_PAGE_FILTER)!);
    } else {
      this.designPageFilter = new PageFilter();
    }
    return this.designPageFilter;
  }

  setDesignPageFilter(pageFilter: PageFilter) {
    this.designPageFilter = pageFilter;
    localStorage.setItem(this.DESIGN_PAGE_FILTER, JSON.stringify(this.designPageFilter));
  }

  getWebPageFilter(): PageFilter {
    if (localStorage.getItem(this.WEB_PAGE_FILTER)) {
      this.webPageFilter = JSON.parse(localStorage.getItem(this.WEB_PAGE_FILTER)!);
    } else {
      this.webPageFilter = new PageFilter();
    }
    return this.webPageFilter;
  }

  setWebPageFilter(pageFilter: PageFilter) {
    this.webPageFilter = pageFilter;
    localStorage.setItem(this.WEB_PAGE_FILTER, JSON.stringify(this.webPageFilter));
  }

  getMobilePageFilter(): PageFilter {
    if (localStorage.getItem(this.MOBILE_PAGE_FILTER)) {
      this.mobilePageFilter = JSON.parse(localStorage.getItem(this.MOBILE_PAGE_FILTER)!);
    } else {
      this.mobilePageFilter = new PageFilter();
    }
    return this.mobilePageFilter;
  }

  setMobilePageFilter(pageFilter: PageFilter) {
    this.mobilePageFilter = pageFilter;
    localStorage.setItem(this.MOBILE_PAGE_FILTER, JSON.stringify(this.mobilePageFilter));
  }

  getMarketingPageFilter(): PageFilter {
    if (localStorage.getItem(this.MARKETING_PAGE_FILTER)) {
      this.marketingPageFilter = JSON.parse(localStorage.getItem(this.MARKETING_PAGE_FILTER)!);
    } else {
      this.marketingPageFilter = new PageFilter();
    }
    return this.marketingPageFilter;
  }

  setMarketingPageFilter(pageFilter: PageFilter) {
    this.marketingPageFilter = pageFilter;
    localStorage.setItem(this.MARKETING_PAGE_FILTER, JSON.stringify(this.marketingPageFilter));
  }

  getUsersPageFilter(): PageFilter {
    if (localStorage.getItem(this.USERS_PAGE_FILTER)) {
      this.usersPageFilter = JSON.parse(localStorage.getItem(this.USERS_PAGE_FILTER)!);
    } else {
      this.usersPageFilter = new PageFilter();
    }
    return this.usersPageFilter;
  }

  setUsersPageFilter(pageFilter: PageFilter) {
    this.usersPageFilter = pageFilter;
    localStorage.setItem(this.USERS_PAGE_FILTER, JSON.stringify(this.usersPageFilter));
  }

  getUsersUserFilter(): UserFilter {
    if (localStorage.getItem(this.USERS_USER_FILTER)) {
      this.usersUserFilter = JSON.parse(localStorage.getItem(this.USERS_USER_FILTER)!);
    } else {
      this.usersUserFilter = new UserFilter();
    }
    return this.usersUserFilter;
  }

  setUsersUserFilter(usersUserFilter: UserFilter) {
    this.usersUserFilter = usersUserFilter;
    localStorage.setItem(this.USERS_USER_FILTER, JSON.stringify(this.usersUserFilter));
  }
}
