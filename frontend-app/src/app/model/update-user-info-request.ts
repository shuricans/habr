export class UpdateUserInfoRequest {
  constructor(public firstName: string,
              public lastName: string,
              public aboutMe: string,
              public birthday: string) {
  }
}
