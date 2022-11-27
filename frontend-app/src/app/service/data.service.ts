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

  private habrPageFilter!: PageFilter;
  private lkPostPageFilter!: PageFilter;
  private lkActiveComponent!: number;
  private lkPostFilter!: PostFilterOwn;

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
  }
}
