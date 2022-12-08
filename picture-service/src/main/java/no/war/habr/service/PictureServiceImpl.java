package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.persist.model.Picture;
import no.war.habr.persist.repository.PictureRepository;
import no.war.habr.service.dto.PictureData;
import no.war.habr.service.dto.PictureDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * PictureService implementation
 *
 * @author Karachev Sasha
 * @see PictureService
 */
@RequiredArgsConstructor
@Service
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;

    @Value("${app.picture.storage.path}")
    private String storagePath;

    /**
     * Returns PictureDto by id
     *
     * @param  id picture id
     * @return {@code Optional&lt;PictureDto&gt;}
     */
    @Override
    public Optional<PictureDto> getPictureDataById(long id) {

        return pictureRepository.findById(id)
                .map(pic -> new PictureDto(
                        pic.getContentType(),
                        Paths.get(storagePath, pic.getStorageFileName())))
                .filter(pic -> Files.exists(pic.getPath()))
                .map(pic -> {
                    try {
                        pic.setData(Files.readAllBytes(pic.getPath()));
                        return pic;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    /**
     * Saves picture to the storage.
     *
     * @param  pictureData {@code byte[]} array, actually image
     * @return {@code String} file name under storage
     */
    @Override
    public String createPicture(byte[] pictureData) {
        String fileName = UUID.randomUUID().toString();

        try (OutputStream os = Files.newOutputStream(Paths.get(storagePath, fileName))) {
            os.write(pictureData);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return fileName;
    }

    /**
     * Saves images to storage and saves metadata to database.
     *
     * @param  pictures {@code List&lt;MultipartFile&gt;}
     * @return {@code List&lt;PictureData&gt;} - images ids
     */
    @Transactional
    @Override
    public List<PictureData> savePictures(List<MultipartFile> pictures) {
        List<PictureData> resultSet = new ArrayList<>();

        for (MultipartFile pic : pictures) {

            if (pic.isEmpty()) {
                continue;
            }

            try {
                Picture picture = Picture.builder()
                        .name(pic.getOriginalFilename())
                        .contentType(pic.getContentType())
                        .storageFileName(createPicture(pic.getBytes()))
                        .build();
                Picture savedPic = pictureRepository.save(picture);
                PictureData pictureData = PictureData.builder()
                        .id(savedPic.getId())
                        .name(savedPic.getName())
                        .build();
                resultSet.add(pictureData);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return resultSet;
    }

    /**
     * Returns Picture entity by id
     *
     * @param  id picture id
     * @return {@code Optional&lt;Picture&gt;}
     */
    @Override
    public Optional<Picture> getPictureById(long id) {

        return pictureRepository.findById(id);
    }
}
