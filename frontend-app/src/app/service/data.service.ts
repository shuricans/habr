import { Injectable } from '@angular/core';
import { PageFilter } from '../model/page-filter';
import { PostFilterOwn } from '../model/post-filter-own';

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

  private habrPageFilter!: PageFilter;
  private lkPostPageFilter!: PageFilter;
  private lkActiveComponent!: number;
  private lkPostFilter!: PostFilterOwn;
  private designPageFilter!: PageFilter;
  private webPageFilter!: PageFilter;
  private mobilePageFilter!: PageFilter;

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

  getMobilePageFilter() {
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
}
