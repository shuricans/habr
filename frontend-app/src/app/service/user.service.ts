import {Injectable} from '@angular/core';
import {HttpClient, HttpContext, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {UserDto} from "../model/user-dto";
import {UpdateUserInfoRequest} from "../model/update-user-info-request";
import { BYPASS_LOG } from '../interceptor/token.interceptor';
import { PageOfUsers } from '../model/PageOfUsers';
import { PageFilter } from '../model/page-filter';
import { UserFilter } from '../model/user-filter';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {
  }

  public getUserData(): Observable<UserDto> {
    return this.http.get<UserDto>('api/v1/users/me');
  }

  public updateUserInfo(updateUserInfoRequest: UpdateUserInfoRequest): Observable<UserDto> {
    return this.http.patch<UserDto>('api/v1/users/update', updateUserInfoRequest)
  }

  public getActiveUserByUsername(username: string): Observable<UserDto> {
    return this.http.get<UserDto>(`api/v1/users/username-active/${username}`, {
      context: new HttpContext().set(BYPASS_LOG, true) 
    });
  }

  public getAllUsers(pageFilter?: PageFilter, userFilter?: UserFilter): Observable<PageOfUsers> {
    
    let params = new HttpParams();

    if(userFilter?.username != null) {
      params = params.set('username', userFilter.username);
    }
    if(userFilter?.firstName != null) {
      params = params.set('firstName', userFilter.firstName);
    }
    if(userFilter?.lastName != null) {
      params = params.set('lastName', userFilter.lastName);
    }
    if(userFilter?.condition != null) {
      params = params.set('condition', userFilter.condition);
    }

    if(pageFilter?.page != null) {
      params = params.set('page', pageFilter.page);
    }
    if(pageFilter?.size != null) {
      params = params.set('size', pageFilter.size);
    }
    if(pageFilter?.sortDir != null) {
      params = params.set('sortDir', pageFilter.sortDir);
    }
    if(pageFilter?.sortField != null) {
      params = params.set('sortField', pageFilter.sortField);
    }

    return this.http.get<PageOfUsers>('api/v1/users', { params });
  }
}
