import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { BYPASS_LOG } from '../interceptor/token.interceptor';
import { PictureData } from '../model/picture-data';

@Injectable({
  providedIn: 'root'
})
export class PictureService {

  private readonly getUrl: string = 'picture/';
  private readonly uploadUrl: string = 'api/v1/upload-pictures/';

  constructor(private http: HttpClient,
              private router: Router) {
  }

  public getPicture(picId: number): Observable<Blob> {
    return this.http.get(this.getUrl + picId, {
      responseType: 'blob',
      context: new HttpContext().set(BYPASS_LOG, true)
    });
  }

  public uploadPictures(formData: FormData): Observable<PictureData[]> {
    return this.http.post<PictureData[]>(this.uploadUrl, formData);
  }

  public getLink(picId: number): string {
       return window.location.origin + '/' + this.getUrl + picId;
  }
}
