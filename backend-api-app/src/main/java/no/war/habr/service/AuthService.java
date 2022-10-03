package no.war.habr.service;

import no.war.habr.payload.request.LoginRequest;
import no.war.habr.payload.response.JwtResponse;

public interface AuthService {

    JwtResponse signIn(LoginRequest loginRequest);
}
