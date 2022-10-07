package no.war.habr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefreshTokenDto {

    private Long id;

    private Long userId;

    private String username;

    private String token;

    private Instant expiryDate;

    private LocalDateTime created;
}
