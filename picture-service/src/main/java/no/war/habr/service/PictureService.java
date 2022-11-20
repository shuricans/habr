package no.war.habr.service;

import no.war.habr.service.dto.PictureDto;

import java.util.Optional;

/**
 * Interface PictureService
 *
 * @see PictureDto
 * @author Karachev Sasha
 */
public interface PictureService {

    Optional<PictureDto> getPictureDataById(long id);

    String createPicture(byte[] pictureData);
}
