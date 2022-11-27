package no.war.habr.service.dto;

import no.war.habr.persist.model.Topic;

/**
 * TopicMapper.
 *
 * @author Zalyaletdinova Ilmira
 */
public interface TopicMapper {
    TopicDto fromTopic(Topic topic);
}
