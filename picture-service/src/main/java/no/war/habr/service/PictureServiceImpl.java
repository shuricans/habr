package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.persist.repository.PictureRepository;
import no.war.habr.service.dto.PictureDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public String createPicture(byte[] pictureData) {
        String filename = UUID.randomUUID().toString();
        try (OutputStream os = Files.newOutputStream(Paths.get(storagePath, filename))) {
            os.write(pictureData);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return filename;
    }
}
