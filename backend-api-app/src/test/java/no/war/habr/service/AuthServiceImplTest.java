package no.war.habr.service;

import no.war.habr.auth.AuthCreator;
import no.war.habr.exception.TokenRefreshException;
import no.war.habr.exception.UserAlreadyExistsException;
import no.war.habr.exception.UserNotFoundException;
import no.war.habr.payload.request.SignupRequest;
import no.war.habr.payload.request.TokenRefreshRequest;
import no.war.habr.payload.response.JwtResponse;
import no.war.habr.payload.response.MessageResponse;
import no.war.habr.payload.response.TokenRefreshResponse;
import no.war.habr.persist.model.ERole;
import no.war.habr.persist.model.Role;
import no.war.habr.persist.model.User;
import no.war.habr.persist.repository.RoleRepository;
import no.war.habr.persist.repository.UserRepository;
import no.war.habr.service.dto.RefreshTokenDto;
import no.war.habr.util.user.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Optional;
import java.util.UUID;

import static no.war.habr.auth.AuthCreator.*;
import static no.war.habr.util.token.RefreshTokenCreator.createRefreshTokenDto;
import static no.war.habr.util.user.UserCreator.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for AuthServiceImpl")
class AuthServiceImplTest {

    private AuthService underTest;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtEncoder jwtEncoder;


    @BeforeEach
    void setUp() {
        underTest = new AuthServiceImpl(
                authenticationManager,
                refreshTokenService,
                passwordEncoder,
                userRepository,
                roleRepository,
                jwtEncoder);
    }

    @Test
    @DisplayName("signIn Authenticate And Returns Jwt Response When Successful")
    void signIn_AuthenticateAndReturnsJwtResponse_WhenSuccessful() {
        // given
        JwtResponse expectedJwtResponse = createJwtResponse();

        Authentication authenticationMock = mock(Authentication.class);

        given(authenticationMock.getName())
                .willReturn("");

        given(authenticationManager.authenticate(any(Authentication.class)))
                .willReturn(authenticationMock);

        Jwt jwtMock = mock(Jwt.class);

        given(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .willReturn(jwtMock);

        given(jwtMock.getTokenValue())
                .willReturn(AuthCreator.TOKEN);

        User user = createUser();

        given(userRepository.findByUsername(any(String.class)))
                .willReturn(Optional.of(user));

        given(refreshTokenService.create(any(User.class)))
                .willReturn(mock(RefreshTokenDto.class));

        // when
        JwtResponse jwtResponse = underTest.signIn(createLoginRequest());

        // then
        assertThat(jwtResponse.getToken()).isEqualTo(expectedJwtResponse.getToken());
    }

    @Test
    @DisplayName("signUp Persists User When Successful")
    void signUp_PersistsUser_WhenSuccessful() {
        // given
        String expectedMessage = "User registered successfully!";

        given(roleRepository.findByName(any(ERole.class)))
                .willReturn(mock(Role.class));

        SignupRequest signupRequest = createSignupRequest();

        // when
        MessageResponse messageResponse = underTest.signUp(signupRequest);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isNotNull();
        assertThat(capturedUser.getUsername()).isEqualTo(signupRequest.getUsername());

        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("signUp Throws UserAlreadyExistsException When User Already Exists")
    void signUp_ThrowsUserAlreadyExistsException_WhenUserAlreadyExists() {
        // given
        SignupRequest signupRequest = createSignupRequest();
        given(userRepository.existsByUsername(ArgumentMatchers.anyString()))
                .willReturn(true);
        // when
        // then
        assertThatThrownBy(() -> underTest.signUp(signupRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with username [%s] already exists",
                        signupRequest.getUsername());
    }

    @Test
    @DisplayName("refreshToken Returns TokenRefreshResponse When Successful")
    void refreshToken_ReturnsTokenRefreshResponse_WhenSuccessful() {
        // given
        TokenRefreshResponse expectedResponse = createTokenRefreshResponse();
        RefreshTokenDto refreshTokenDto = createRefreshTokenDto();

        given(refreshTokenService.findByToken(ArgumentMatchers.anyString()))
                .willReturn(Optional.of(refreshTokenDto));

        given(refreshTokenService.verifyExpiration(ArgumentMatchers.any(RefreshTokenDto.class)))
                .willReturn(refreshTokenDto);

        User user = createUser();

        given(userRepository.findByUsername(any(String.class)))
                .willReturn(Optional.of(user));

        Jwt jwtMock = mock(Jwt.class);

        given(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .willReturn(jwtMock);

        given(jwtMock.getTokenValue())
                .willReturn(AuthCreator.TOKEN);

        // when
        TokenRefreshResponse tokenRefreshResponse = underTest.refreshToken(createTokenRefreshRequest());

        // then
        assertThat(tokenRefreshResponse).isNotNull();

        assertThat(tokenRefreshResponse.getRefreshToken()).isEqualTo(expectedResponse.getRefreshToken());

        assertThat(tokenRefreshResponse.getAccessToken()).isEqualTo(expectedResponse.getAccessToken());
    }

    @Test
    @DisplayName("refreshToken Throws TokenRefreshException When Refresh Token Not Found")
    void refreshToken_ThrowsTokenRefreshException_WhenRefreshTokenNotFound() {
        // given
        TokenRefreshRequest tokenRefreshRequest = createTokenRefreshRequest();

        given(refreshTokenService.findByToken(ArgumentMatchers.anyString()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.refreshToken(tokenRefreshRequest))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageContaining("Failed for [%s]: %s",
                        tokenRefreshRequest.getRefreshToken(), "Refresh token is not in database!");
    }

    @Test
    @DisplayName("logout Removes Refresh Token When Successful")
    void logout_RemovesRefreshToken_WhenSuccessful() {
        // given
        String expectedMessage = "Log out successful";
        User user = createUser();
        given(userRepository.findByUsername(any(String.class)))
                .willReturn(Optional.of(user));

        // when
        MessageResponse messageResponse = underTest.logout(ArgumentMatchers.anyString());

        // then
        ArgumentCaptor<Long> userIdArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(refreshTokenService).deleteByUserId(userIdArgumentCaptor.capture());
        Long capturedUserId = userIdArgumentCaptor.getValue();

        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse.getMessage()).isEqualTo(expectedMessage);

        assertThat(capturedUserId).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("logout Throws UserNotFoundException When User Does Not Exists")
    void logout_ThrowsUserNotFoundException_WhenUserDoesNotExists() {
        // given
        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.logout(USERNAME))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User [%s] not found.", USERNAME);
    }
}