import { Pageable } from "./pageable";
import { UserDto } from "./user-dto";

export class PageOfUsers {
  constructor(public content: UserDto[],
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