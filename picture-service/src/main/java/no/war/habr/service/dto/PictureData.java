package no.war.habr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Karachev Sasha
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PictureData {

    private Long id;

    private String name;
}
