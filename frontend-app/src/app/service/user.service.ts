import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {UserDto} from "../model/user-dto";
import {UpdateUserInfoRequest} from "../model/update-user-info-request";

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
}
