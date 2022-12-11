package no.war.habr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String aboutMe;
    private LocalDate birthday;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String condition;
    private Set<String> roles;
}
