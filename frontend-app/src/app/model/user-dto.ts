export class UserDto {
  constructor(public id: number,
              public username: string,
              public firstName: string,
              public lastName: string,
              public aboutMe: string,
              public birthday: string,
              public created: string,
              public updated: string,
              public condition: string,
              public roles: string[],
  ) {
  }
}
