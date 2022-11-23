package no.war.habr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TopicDto represents Topic entity.
 *
 * @author Zalyaletdinova Ilmira
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TopicDto {
    private Long id;
    private String name;
}
