package no.war.habr.persist.repository;

import no.war.habr.persist.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface - PictureRepository.
 *
 * @author Karachev Sasha
 */
public interface PictureRepository extends JpaRepository<Picture, Long> {
}
