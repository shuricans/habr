import {Injectable} from '@angular/core';
import { catchError, map, Observable, of, tap} from "rxjs";
import {Credentials} from "../model/credentials";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import { JwtResponse } from '../model/jwt-response';
import { AuthResult } from '../model/authResult';
import { ExceptionDetails } from '../model/exception-details';
import { MessageResponse } from '../model/message-response';
import { TokenRefreshResponse } from '../model/token-refresh-response';
import { UserModel } from '../model/user-model';
import jwtDecode from 'jwt-decode';
import { SignupRequest } from '../model/signup-request';
import { SignupResult } from '../model/signupResult';
import { DataService } from './data.service';
import { MessageService } from './message.service';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly roleUser = 'ROLE_USER';
  private readonly roleModerator = 'ROLE_MODERATOR';
  private readonly roleAdmin = 'ROLE_ADMIN';
  private readonly JWT_TOKEN = 'JWT_TOKEN';
  private readonly REFRESH_TOKEN = 'REFRESH_TOKEN';
  private readonly TYPE = 'TYPE';
  private readonly DEFAULT_TYPE = 'Bearer';
  public redirectUrl?: string;

  user: UserModel | null;

  constructor(private http: HttpClient, 
              private dataService: DataService,
              private messageService: MessageService) {
    this.user = this.getUser(this.getJwtToken()!);
  }

  private handleError(error: HttpErrorResponse): ExceptionDetails {
    if (error.status === 0) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error);
      const exceptionDetails: ExceptionDetails = {
        title: 'A client-side or network error',
        details: error.message,
        developerMessage: '',
        status: 0,
        timestamp: 'fucking now!'
      };
      return exceptionDetails;
    }
    // The backend returned an unsuccessful response code.
    // The response body may contain clues as to what went wrong.
    console.error(
      `Backend returned code ${error.status}, body was: `, error.error);
    const exceptionDetails = error.error as ExceptionDetails;
    return exceptionDetails as ExceptionDetails;
  }

  signin(credentials: Credentials): Observable<AuthResult> {
    return this.http.post<JwtResponse>('api/v1/auth/signin', credentials)
      .pipe(
        tap(jwtResponse => this.doLoginUser(jwtResponse)),
        tap(jwtResponse => this.user = this.getUser(jwtResponse.token)),
        map(jwtResponse => {
          let authResult = new AuthResult();
          authResult.success = true;
          authResult.jwtResponse = jwtResponse;
          authResult.redirectUrl = this.redirectUrl ?? '/lk';
          return authResult;
        }),
        catchError((error: HttpErrorResponse) => {
          const exceptionDetails = this.handleError(error);
          let authResult = new AuthResult();
          authResult.success = false;
          authResult.exceptionDetails = exceptionDetails;
          return of(authResult);
        }));
  }

  signup(credentials: SignupRequest): Observable<SignupResult> {
    return this.http.post<MessageResponse>('api/v1/auth/signup', credentials)
      .pipe(
        map(() => {
          let signupResult = new SignupResult();
          signupResult.success = true;
          return signupResult;
        }),
        catchError((error: HttpErrorResponse) => {
          const exceptionDetails = this.handleError(error);
          let signupResult = new SignupResult();
          signupResult.success = false;
          signupResult.exceptionDetails = exceptionDetails;
          return of(signupResult);
        }));
  }

  logout() {
    return this.http.delete<MessageResponse>('api/v1/auth/logout')
    .pipe(
      tap(() => this.doLogoutUser()),
      map(() => true),
      catchError(error => {
        alert(error.error);
        return of(false);
      }));
  }

  isLoggedIn(): boolean {
    return !!this.getJwtToken();
  }

  refreshToken(): Observable<TokenRefreshResponse> {
    return this.http.post<TokenRefreshResponse>('api/v1/auth/refresh', {
      'refreshToken': this.getRefreshToken()
    }).pipe(
      tap((response: TokenRefreshResponse) => {
        this.storeJwtToken(response.accessToken);
      }));
  }

  getJwtToken() {
    return localStorage.getItem(this.JWT_TOKEN);
  }

  getType(): string {
    if (localStorage.getItem(this.TYPE))  {
      return localStorage.getItem(this.TYPE)!;
    }
    return this.DEFAULT_TYPE;
  }

  public isAdmin(): boolean {
    if (!this.isLoggedIn()) {
      return false;
    }
    return this.user!.scope.includes(this.roleAdmin);
  }

  public isModerator() {
    if (!this.isLoggedIn()) {
      return false;
    }
    return this.user!.scope.includes(this.roleModerator);
  }

  public isModeratorOrAdmin() {
    if (!this.isLoggedIn()) {
      return false;
    }
    return this.user!.scope.includes(this.roleModerator) ||
           this.user!.scope.includes(this.roleAdmin);
  }

  private doLoginUser(jwtResponse: JwtResponse) {
    this.storeTokens(jwtResponse);
  }

  public doLogoutUser() {
    this.user = null;
    this.removeTokens();
    this.dataService.clearAllData();
    this.messageService.sendMessage('updateSecondHeader');   
  }

  private getRefreshToken() {
    return localStorage.getItem(this.REFRESH_TOKEN);
  }

  private storeJwtToken(jwt: string) {
    localStorage.setItem(this.JWT_TOKEN, jwt);
  }

  private storeTokens(jwtResponse: JwtResponse) {
    localStorage.setItem(this.JWT_TOKEN, jwtResponse.token);
    localStorage.setItem(this.REFRESH_TOKEN, jwtResponse.refreshToken);
    localStorage.setItem(this.TYPE, jwtResponse.type);
  }

  private removeTokens() {
    localStorage.removeItem(this.JWT_TOKEN);
    localStorage.removeItem(this.REFRESH_TOKEN);
    localStorage.removeItem(this.TYPE);
  }

  private getUser(token: string): UserModel | null {
    if (!token) {
      return null
    }
    return jwtDecode(token) as UserModel;
  }
}

