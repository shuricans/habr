package no.war.habr.service;

import no.war.habr.exception.TokenRefreshException;
import no.war.habr.persist.model.RefreshToken;
import no.war.habr.persist.model.User;
import no.war.habr.persist.repository.RefreshTokenRepository;
import no.war.habr.persist.repository.UserRepository;
import no.war.habr.service.dto.RefreshTokenDto;
import no.war.habr.service.dto.RefreshTokenMapper;
import no.war.habr.util.token.RefreshTokenCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static no.war.habr.util.token.RefreshTokenCreator.createRefreshToken;
import static no.war.habr.util.token.RefreshTokenCreator.createRefreshTokenDto;
import static no.war.habr.util.user.UserCreator.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    private RefreshTokenService underTest;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RefreshTokenMapper refreshTokenMapper;

    @BeforeEach
    void setUp() {
        underTest = new RefreshTokenServiceImpl(refreshTokenRepository,
                userRepository, refreshTokenMapper);
    }

    @Test
    @DisplayName("listAll Returns List Of Refresh Token Inside Page Object When Successful")
    void listAll_ReturnsListOfRefreshTokenInsidePageObject_WhenSuccessful() {
        // given
        RefreshToken refreshToken = createRefreshToken();
        RefreshTokenDto expectedRefreshTokenDto = createRefreshTokenDto();

        PageImpl<RefreshToken> tokensPage = new PageImpl<>(List.of(refreshToken));

        given(refreshTokenRepository.findAll(any(Pageable.class)))
                .willReturn(tokensPage);

        given(refreshTokenMapper.fromRefreshToken(any(RefreshToken.class)))
                .willReturn(expectedRefreshTokenDto);

        // when
        Page<RefreshTokenDto> refreshTokenDtoPage = underTest.listAll(PageRequest.of(0, 1));

        // then
        assertThat(refreshTokenDtoPage)
                .isNotEmpty()
                .hasSize(1)
                .contains(expectedRefreshTokenDto);
    }

    @Test
    @DisplayName("listAllByUser Returns List Of User Token Inside Page Object When Successful")
    void listAllByUser_ReturnsListOfUserTokenInsidePageObject_WhenSuccessful() {
        // given
        RefreshToken refreshToken = createRefreshToken();
        RefreshTokenDto expectedRefreshTokenDto = createRefreshTokenDto();
        User user = createUser();
        PageImpl<RefreshToken> tokensPage = new PageImpl<>(List.of(refreshToken));

        given(userRepository.findById(any(Long.class)))
                .willReturn(Optional.of(user));

        given(refreshTokenRepository.findAllByUser(any(Pageable.class), any(User.class)))
                .willReturn(tokensPage);

        given(refreshTokenMapper.fromRefreshToken(any(RefreshToken.class)))
                .willReturn(expectedRefreshTokenDto);

        PageRequest pageRequest = PageRequest.of(0, 1);

        // when
        Page<RefreshTokenDto> refreshTokenDtoPage = underTest
                .listAllByUser(pageRequest, user.getId());


        // then
        ArgumentCaptor<Long> userIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(userRepository)
                .findById(userIdArgumentCaptor.capture());

        verify(refreshTokenRepository)
                .findAllByUser(pageableArgumentCaptor.capture(), userArgumentCaptor.capture());

        Long capturedUserId = userIdArgumentCaptor.getValue();
        User capturedUser = userArgumentCaptor.getValue();
        Pageable capturedPageable = pageableArgumentCaptor.getValue();

        assertThat(capturedUserId).isEqualTo(user.getId());
        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedPageable).isEqualTo(pageRequest);

        assertThat(refreshTokenDtoPage)
                .isNotEmpty()
                .contains(expectedRefreshTokenDto);
    }

    @Test
    @DisplayName("findByToken Returns Refresh Token Inside Optional Object When Successful")
    void findByToken_ReturnsRefreshTokenInsideOptionalObject_WhenSuccessful() {
        // given
        // when
        underTest.findByToken(RefreshTokenCreator.TOKEN);

        // then
        ArgumentCaptor<String> tokenArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(refreshTokenRepository).findByToken(tokenArgumentCaptor.capture());

        String capturedToken = tokenArgumentCaptor.getValue();

        assertThat(capturedToken).isEqualTo(RefreshTokenCreator.TOKEN);
    }

    @Test
    @DisplayName("create Persists Refresh Token When Successful")
    void create_PersistsRefreshToken_WhenSuccessful() {
        // given
        User user = createUser();

        // when
        underTest.create(user);

        // then
        ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        verify(refreshTokenRepository).save(refreshTokenArgumentCaptor.capture());

        RefreshToken capturedRefreshToken = refreshTokenArgumentCaptor.getValue();

        assertThat(capturedRefreshToken.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("verifyExpiration Checks Token Expiration And Returns Token When Successful")
    void verifyExpiration_ChecksTokenExpirationAndReturnsToken_WhenSuccessful() {
        // given
        RefreshTokenDto refreshTokenDto = createRefreshTokenDto();

        // when
        RefreshTokenDto checkedToken = underTest.verifyExpiration(refreshTokenDto);

        // then
        assertThat(checkedToken).isEqualTo(refreshTokenDto);
    }

    @Test
    @DisplayName("verifyExpiration Throws TokenRefreshException When Token Expired")
    void verifyExpiration_ThrowsTokenRefreshException_WhenTokenExpired() {
        // given
        RefreshTokenDto refreshTokenDto = createRefreshTokenDto();

        refreshTokenDto.setExpiryDate(Instant.now().minusSeconds(10));

        // when
        // then
        assertThatThrownBy(() -> underTest.verifyExpiration(refreshTokenDto))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageContaining("Refresh token was expired. Please make a new signin request");

        ArgumentCaptor<Long> tokenIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(refreshTokenRepository).deleteById(tokenIdArgumentCaptor.capture());

        Long capturedTokenId = tokenIdArgumentCaptor.getValue();

        assertThat(capturedTokenId).isEqualTo(refreshTokenDto.getId());
    }

    @Test
    @DisplayName("deleteByUserId Removes Token When Successful")
    void deleteByUserId_RemovesToken_WhenSuccessful() {
        // given
        User user = createUser();
        given(userRepository.findById(any(Long.class)))
                .willReturn(Optional.of(user));
        // when
        underTest.deleteByUserId(user.getId());

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(refreshTokenRepository).deleteByUser(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
    }
}