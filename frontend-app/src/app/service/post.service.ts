import { HttpClient, HttpContext, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BYPASS_LOG } from '../interceptor/token.interceptor';
import { Page } from '../model/page';
import { PageFilter } from '../model/page-filter';
import { PostDto } from '../model/post-dto';
import { PostFilter } from '../model/post-filter';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private http: HttpClient) {

  }

  public findById(postId: number): Observable<PostDto> {
    return this.http.get<PostDto>('api/v1/posts/' + postId, { 
      context: new HttpContext().set(BYPASS_LOG, true) 
    })
  }

  public findAllPublishedPost(pageFilter?: PageFilter, postFilter?: PostFilter): Observable<Page> {

    let params = new HttpParams();

    if(postFilter?.topic != null) {
      params = params.set('topic', postFilter.topic)
    }
    if(postFilter?.tag != null) {
      params = params.set('tag', postFilter.tag)
    }

    if(pageFilter?.page != null) {
      params = params.set('page', pageFilter.page)
    }
    if(pageFilter?.size != null) {
      params = params.set('size', pageFilter.size)
    }
    if(pageFilter?.sortDir != null) {
      params = params.set('sortDir', pageFilter.sortDir)
    }
    if(pageFilter?.sortField != null) {
      params = params.set('sortField', pageFilter.sortField)
    }

    return this.http.get<Page>('api/v1/posts', { 
      params,
      context: new HttpContext().set(BYPASS_LOG, true) 
    });
  }
}
