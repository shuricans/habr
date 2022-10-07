package no.war.habr.service.dto;

import no.war.habr.persist.model.RefreshToken;

public interface RefreshTokenMapper {

    RefreshTokenDto fromRefreshToken(RefreshToken refreshToken);
}
