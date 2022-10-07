package no.war.habr.service;

import no.war.habr.persist.model.User;
import no.war.habr.service.dto.RefreshTokenDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RefreshTokenService {

    Page<RefreshTokenDto> listAll(Pageable pageable);

    Page<RefreshTokenDto> listAllByUser(Pageable pageable, Long userId);

    Optional<RefreshTokenDto> findByToken(String token);

    RefreshTokenDto create(User user);

    RefreshTokenDto verifyExpiration(RefreshTokenDto token);

    void deleteByUserId(Long userId);
}
