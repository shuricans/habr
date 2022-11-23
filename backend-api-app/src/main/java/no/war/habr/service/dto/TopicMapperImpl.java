package no.war.habr.service.dto;

import no.war.habr.persist.model.Topic;
import org.springframework.stereotype.Component;

/**
 * TopicMapperImpl.
 *
 * @author Zalyaletdinova Ilmira
 */
@Component
public class TopicMapperImpl implements TopicMapper{
    @Override
    public TopicDto fromTopic(Topic topic) {
        return TopicDto.builder()
                .id(topic.getId())
                .name(topic.getName())
                .build();
    }
}
