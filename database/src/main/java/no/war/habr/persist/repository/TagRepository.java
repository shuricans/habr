package no.war.habr.persist.repository;

import no.war.habr.persist.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
