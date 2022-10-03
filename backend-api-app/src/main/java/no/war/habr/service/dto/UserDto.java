package no.war.habr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDto {

    @NotNull()
    @Min(value = 1)
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    private String aboutMe;

    @Past
    private LocalDate birthday;

    @NotBlank
    @Size(max = 20)
    private String condition;

    @NotEmpty
    private Set<String> roles;
}
