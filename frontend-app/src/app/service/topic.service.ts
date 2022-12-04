import {Injectable} from '@angular/core';
import {HttpClient, HttpContext} from "@angular/common/http";
import {BYPASS_LOG} from "../interceptor/token.interceptor";
import {Observable} from "rxjs";
import {TopicDto} from "../model/topic-dto";

@Injectable({
  providedIn: 'root'
})
export class TopicService {

  readonly topicLink: Record<string, string> = {
    'Мобильная разработка': '/mobile',
    'Веб-разработка': '/web',
    'Дизайн': '/design',
    'Маркетинг': '/marketing',
  }

  constructor(private http: HttpClient) {
  }

  public findAllTopics(): Observable<TopicDto[]> {
    return this.http.get<TopicDto[]>('api/v1/topics', {
      context: new HttpContext().set(BYPASS_LOG, true)
    });
  }
}
