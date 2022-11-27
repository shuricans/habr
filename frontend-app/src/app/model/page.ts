import { Pageable } from "./pageable";
import { PostDto } from "./post-dto";

export class Page {
  constructor(public content: PostDto[],
              public pageable: Pageable,
              public totalPages: number,
              public totalElements: number,
              public last: boolean,
              public number: number,
              public first: boolean,
              public numberOfElements: number,
              public size: number,
              public empty: boolean) {
  }
}