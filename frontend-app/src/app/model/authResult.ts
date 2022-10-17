import { ExceptionDetails } from "./exception-details";
import { JwtResponse } from "./jwt-response";

export class AuthResult {

    public success: boolean = false;
    public jwtResponse?: JwtResponse;
    public exceptionDetails?: ExceptionDetails;
    public redirectUrl?: string;
    public callbackAfterSuccess?: () => void;
}