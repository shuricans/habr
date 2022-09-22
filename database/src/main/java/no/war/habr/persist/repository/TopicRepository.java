package no.war.habr.persist.repository;

import no.war.habr.persist.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
