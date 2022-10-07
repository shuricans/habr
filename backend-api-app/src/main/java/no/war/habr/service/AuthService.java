package no.war.habr.service;

import no.war.habr.payload.request.LoginRequest;
import no.war.habr.payload.request.SignupRequest;
import no.war.habr.payload.request.TokenRefreshRequest;
import no.war.habr.payload.response.JwtResponse;
import no.war.habr.payload.response.MessageResponse;
import no.war.habr.payload.response.TokenRefreshResponse;

public interface AuthService {

    JwtResponse signIn(LoginRequest loginRequest);

    MessageResponse signUp(SignupRequest signUpRequest);

    TokenRefreshResponse refreshToken(TokenRefreshRequest request);

    MessageResponse logout(String username);
}
