import { PictureData } from "./picture-data";

export class PostDto {
    constructor(public id: number,
                public created: string,
                public updated: string,
                public title: string,
                public content: string,
                public description: string,
                public mainPictureId: number,
                public condition: string,
                public owner: string,
                public topic: string,
                public pictures: PictureData[],
                public tags: string[],
    ) {
  }
}
