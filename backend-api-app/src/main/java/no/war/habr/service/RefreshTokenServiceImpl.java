package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.exception.TokenRefreshException;
import no.war.habr.exception.UserNotFoundException;
import no.war.habr.persist.model.RefreshToken;
import no.war.habr.persist.model.User;
import no.war.habr.persist.repository.RefreshTokenRepository;
import no.war.habr.persist.repository.UserRepository;
import no.war.habr.service.dto.RefreshTokenDto;
import no.war.habr.service.dto.RefreshTokenMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Value("${app.jwt.refreshExpirationS: #{0L}}")
    private Long refreshExpirationS = 0L;

    @Override
    public Page<RefreshTokenDto> listAll(Pageable pageable) {
        return refreshTokenRepository
                .findAll(pageable)
                .map(refreshTokenMapper::fromRefreshToken);
    }

    @Override
    public Page<RefreshTokenDto> listAllByUser(Pageable pageable, Long userId) {
        User user = getUserById(userId);
        return refreshTokenRepository
                .findAllByUser(pageable, user)
                .map(refreshTokenMapper::fromRefreshToken);
    }

    @Override
    public Optional<RefreshTokenDto> findByToken(String token) {
        return refreshTokenRepository
                .findByToken(token)
                .map(refreshTokenMapper::fromRefreshToken);
    }

    @Override
    public RefreshTokenDto create(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusSeconds(refreshExpirationS))
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        return refreshTokenMapper.fromRefreshToken(savedToken);
    }


    @Transactional
    @Override
    public RefreshTokenDto verifyExpiration(RefreshTokenDto token) {
        boolean isTokenExpired = token.getExpiryDate().compareTo(Instant.now()) < 0;

        if (isTokenExpired) {
            refreshTokenRepository.deleteById(token.getId());

            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    @Override
    public void deleteByUserId(Long userId) {
        User user = getUserById(userId);

        refreshTokenRepository.deleteByUser(user);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " not found."));
    }
}
