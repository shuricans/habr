package no.war.habr.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

    @Min(1)
    private Long postId;

    @NotBlank
    @Size(min = 5, message = "{validation.name.size.too_short}")
    @Size(max = 255, message = "{validation.name.size.too_long}")
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    @Size(max = 2000, message = "{validation.name.size.too_long}")
    private String description;

    @NotBlank
    @Size(max = 255, message = "{validation.name.size.too_long}")
    private String topic;

    private Set<String> tags;

    @Min(1)
    private Long mainPictureId;

    private Set<@Min(1) Long> picturesIds;
}
