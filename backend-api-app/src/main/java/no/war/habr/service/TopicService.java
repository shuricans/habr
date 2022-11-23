package no.war.habr.service;

import no.war.habr.service.dto.TopicDto;

import java.util.List;

/**
 * TopicService.
 *
 * @author Zalyaletdinova Ilmira
 */
public interface TopicService {
    List<TopicDto> findAll();

}
