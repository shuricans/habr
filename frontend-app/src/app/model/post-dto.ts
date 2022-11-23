export class PostDto {
    constructor(public id: number,
                public created: string,
                public updated: string,
                public title: string,
                public content: string,
                public description: string,
                public condition: string,
                public owner: string,
                public topic: string,
                public tags: string[],
    ) {
  }
}