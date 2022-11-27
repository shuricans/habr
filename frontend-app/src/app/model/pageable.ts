export class Pageable {
  constructor(public pageNumber: number,
              public pageSize: number,
              public offset: number,
              public paged: boolean,
              public unpaged: boolean) {
  }
}