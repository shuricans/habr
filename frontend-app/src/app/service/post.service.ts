import { HttpClient, HttpContext, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BYPASS_LOG } from '../interceptor/token.interceptor';
import { MessageResponse } from '../model/message-response';
import { Page } from '../model/page';
import { PageFilter } from '../model/page-filter';
import { PostDataRequest } from '../model/post-data-request';
import { PostDto } from '../model/post-dto';
import { PostFilter } from '../model/post-filter';
import { PostFilterOwn } from '../model/post-filter-own';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private http: HttpClient) {

  }

  public findPublishedById(postId: number): Observable<PostDto> {
    return this.http.get<PostDto>('api/v1/posts/' + postId, { 
      context: new HttpContext().set(BYPASS_LOG, true) 
    })
  }

  public findAllPublishedPost(pageFilter?: PageFilter, postFilter?: PostFilter): Observable<Page> {

    let params = new HttpParams();

    if(postFilter?.username != null) {
      params = params.set('username', postFilter.username)
    }
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

  public findOwnPosts(pageFilter?: PageFilter, postFilterOwn?: PostFilterOwn): Observable<Page> {

    let params = new HttpParams();

    if(postFilterOwn?.topic != null) {
      params = params.set('topic', postFilterOwn.topic)
    }
    if(postFilterOwn?.tag != null) {
      params = params.set('tag', postFilterOwn.tag)
    }
    if(postFilterOwn?.condition != null) {
      params = params.set('condition', postFilterOwn.condition)
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

    return this.http.get<Page>('api/v1/posts/own', { params });
  }

  public save(postDataRequest: PostDataRequest): Observable<PostDto> {
    return this.http.post<PostDto>('api/v1/posts/save', postDataRequest);
  }

  public hide(postId: number): Observable<MessageResponse> {
    return this.http.patch<MessageResponse>('api/v1/posts/hide/' + postId, {});
  }

  public publish(postId: number): Observable<MessageResponse> {
    return this.http.patch<MessageResponse>('api/v1/posts/publish/' + postId, {});
  }

  public delete(postId: number): Observable<MessageResponse> {
    return this.http.patch<MessageResponse>('api/v1/posts/delete/' + postId, {});
  }
}
