package no.war.habr.service;

import no.war.habr.persist.model.Picture;
import no.war.habr.service.dto.PictureData;
import no.war.habr.service.dto.PictureDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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

    List<PictureData> savePictures(List<MultipartFile> pictures);

    Optional<Picture> getPictureById(long id);
}
