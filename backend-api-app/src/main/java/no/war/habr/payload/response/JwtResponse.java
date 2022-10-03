package no.war.habr.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    @Schema(description = "Token to access protected endpoints")
    private String token;

    @Schema(description = "Type of token")
    @Builder.Default
    private String type = "Bearer";

    @Schema(description = "Token to generate others access tokens")
    private String refreshToken;

    private String username;

    private List<String> authorities;
}
