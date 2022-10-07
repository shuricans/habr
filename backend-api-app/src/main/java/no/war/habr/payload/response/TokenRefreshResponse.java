package no.war.habr.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRefreshResponse {

    @Schema(description = "New token generated")
    private String accessToken;
    @Schema(description = "Token to generate others access tokens")
    private String refreshToken;

    @Schema(description = "Type of token")
    @Builder.Default
    private String tokenType = "Bearer";
}
