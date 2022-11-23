import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DateFormatService {

  constructor() { }

  public toLocaleString(localDateTime: string): string {
    return new Date(localDateTime).toLocaleString();
  }
}
