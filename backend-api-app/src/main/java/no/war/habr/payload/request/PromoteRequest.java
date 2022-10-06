package no.war.habr.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoteRequest {

    @NotNull
    @Min(value = 1)
    private Long userId;

    @NotEmpty
    @Schema(description = "Roles to promote user")
    private Set<String> roles;
}
