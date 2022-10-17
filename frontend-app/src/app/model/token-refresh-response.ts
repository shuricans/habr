export class TokenRefreshResponse {

    constructor(public accessToken: string,
                public refreshToken: string,
                public tokenType: string) {
    }
}