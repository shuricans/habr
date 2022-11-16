package no.war.habr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Set;


/**
 * PostDto represents Post entity.
 *
 * @author Karachev Sasha
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostDto {

    @NotNull()
    @Min(value = 1)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String description;

    @NotBlank
    private String condition;

    @NotBlank
    private String owner;

    @NotBlank
    private String topic;

    private Set<String> tags;
}
