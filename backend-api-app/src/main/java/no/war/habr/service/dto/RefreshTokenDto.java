package no.war.habr.service.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RefreshTokenDto {

    private Long id;

    private Long userId;

    private String username;

    private String token;

    private Instant expiryDate;

    private LocalDateTime created;
}
