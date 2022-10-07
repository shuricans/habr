package no.war.habr.util.token;

import no.war.habr.persist.model.RefreshToken;
import no.war.habr.persist.model.User;
import no.war.habr.service.dto.RefreshTokenDto;
import no.war.habr.util.user.UserCreator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RefreshTokenCreator {

    public static final Long ID = 1L;
    public static final String TOKEN = "refresh-token-test";
    public static final LocalDateTime CREATED = LocalDateTime.now();
    public static final Instant EXPIRY_DATE = Instant.now().plus(1, ChronoUnit.HOURS);
    public static final User USER = UserCreator.createUserToBeSave();

    public static RefreshToken createRefreshToken() {
        return RefreshToken.builder()
                .id(ID)
                .user(USER)
                .token(TOKEN)
                .expiryDate(EXPIRY_DATE)
                .build();
    }

    public static RefreshTokenDto createRefreshTokenDto() {
        return RefreshTokenDto.builder()
                .id(ID)
                .userId(USER.getId())
                .username(USER.getUsername())
                .token(TOKEN)
                .expiryDate(EXPIRY_DATE)
                .created(CREATED)
                .build();
    }
}
