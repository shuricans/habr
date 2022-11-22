import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PostDto } from '../model/post-dto';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private http: HttpClient) {

  }

  public findById(postId: number): Observable<PostDto> {
    return this.http.get<PostDto>('api/v1/posts/' + postId);
  }
}
