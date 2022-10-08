package no.war.habr.auth;

import no.war.habr.payload.request.LoginRequest;
import no.war.habr.payload.request.SignupRequest;
import no.war.habr.payload.request.TokenRefreshRequest;
import no.war.habr.payload.response.JwtResponse;
import no.war.habr.payload.response.TokenRefreshResponse;
import no.war.habr.persist.model.User;
import no.war.habr.util.token.RefreshTokenCreator;
import no.war.habr.util.user.UserCreator;

import java.util.List;

public class AuthCreator {

    public static final String USERNAME = "shuricans";
    public static final String FIRSTNAME = "Sasha";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token-test";
    public static final String TYPE = "Bearer";
    public static final String ROLE_USER = "ROLE_USER";

    public static final User USER = UserCreator.createUser();

    public static LoginRequest createLoginRequest() {
        return LoginRequest
                .builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }

    public static SignupRequest createSignupRequest() {
        return SignupRequest
                .builder()
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRSTNAME)
                .build();
    }

    public static JwtResponse createJwtResponse() {
        return JwtResponse.builder()
                .token(TOKEN)
                .type(TYPE)
                .refreshToken(RefreshTokenCreator.TOKEN)
                .username(USER.getUsername())
                .authorities(List.of(ROLE_USER))
                .build();
    }

    public static TokenRefreshRequest createTokenRefreshRequest() {
        return TokenRefreshRequest.builder()
                .refreshToken(RefreshTokenCreator.TOKEN)
                .build();
    }

    public static TokenRefreshResponse createTokenRefreshResponse() {
        return TokenRefreshResponse.builder()
                .accessToken(TOKEN)
                .refreshToken(RefreshTokenCreator.TOKEN)
                .tokenType(TYPE)
                .build();
    }

}
