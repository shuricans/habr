export class JwtResponse {

    constructor(public token: string,
                public type: string, 
                public username: string,
                public refreshToken: string,
                public authorities: string[]) {
    }
}