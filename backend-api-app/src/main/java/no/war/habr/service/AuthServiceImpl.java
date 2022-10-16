package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.exception.TokenRefreshException;
import no.war.habr.exception.UserAlreadyExistsException;
import no.war.habr.exception.UserNotFoundException;
import no.war.habr.payload.request.LoginRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${app.jwt.expirationS}")
    private int jwtExpirationS;

    private final JwtEncoder encoder;

    @Override
    public JwtResponse signIn(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        String username = authentication.getName();

        User user = getUserByUsername(username);

        String jwtToken = generateToken(user);

        RefreshTokenDto refreshTokenDto = refreshTokenService.create(user);

        return JwtResponse
                .builder()
                .username(username)
                .token(jwtToken)
                .refreshToken(refreshTokenDto.getToken())
                .authorities(authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();
    }

    @Override
    public MessageResponse signUp(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UserAlreadyExistsException(signUpRequest.getUsername());
        }

        Role roleUser = roleRepository.findByName(ERole.ROLE_USER);

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .firstName(signUpRequest.getFirstName())
                .roles(Set.of(roleUser))
                .build();

        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshTokenDto refreshTokenDto = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() ->
                        new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));

        String username = refreshTokenDto.getUsername();

        User user = getUserByUsername(username);

        String accessToken = generateToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(requestRefreshToken)
                .build();
    }


    @Override
    public MessageResponse logout(String username) {
        User user = getUserByUsername(username);

        refreshTokenService.deleteByUserId(user.getId());

        return new MessageResponse("Log out successful");
    }

    private String generateToken(User user) {
        Instant now = Instant.now();

        String authorities = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtExpirationS))
                .subject(user.getUsername())
                .claims(claims -> {
                    claims.put("id", user.getId());
                    claims.put("username", user.getUsername());
                    claims.put("firstName", user.getFirstName());
                    claims.put("scope", authorities);
                })
                .build();

        return encoder
                .encode(JwtEncoderParameters.from(jwtClaimsSet))
                .getTokenValue();
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User [" + username + "] not found."));
    }
}
