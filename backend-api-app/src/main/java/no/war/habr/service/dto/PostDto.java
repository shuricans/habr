package no.war.habr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private Long id;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String title;

    private String content;

    private String description;

    private String condition;

    private String owner;

    private String topic;

    private Set<String> tags;
}
