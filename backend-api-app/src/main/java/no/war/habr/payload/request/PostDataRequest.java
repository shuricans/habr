package no.war.habr.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * This class contains necessary fields of post entity for save/update events.
 *
 * @author Karachev Sasha
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostDataRequest {

    private Long postId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String description;

    @NotBlank
    private String topic;

    private Set<String> tags;
}
