package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.payload.request.LoginRequest;
import no.war.habr.payload.response.JwtResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    final AuthenticationManager authenticationManager;

    @Value("${app.jwt.expirationMs}")
    private int jwtExpirationMs;

    private final JwtEncoder encoder;

    @Override
    public JwtResponse signIn(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        Instant now = Instant.now();

        String username = authentication.getName();

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtExpirationMs))
                .subject(username)
                .claim("scope", scope)
                .build();

        String jwtToken = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        //TODO refresh token
        return JwtResponse
                .builder()
                .username(username)
                .token(jwtToken)
                .refreshToken("refreshToken")
                .authorities(authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();
    }
}
