package no.war.habr.service.dto;

import lombok.*;

import java.nio.file.Path;

/**
 * @author Karachev Sasha
 */
@RequiredArgsConstructor
@Setter
@Getter
public class PictureDto {

    private final String contentType;

    private final Path path;

    private byte[] data;
}