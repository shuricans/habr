package no.war.habr.service;

import no.war.habr.persist.repository.TopicRepository;
import no.war.habr.service.dto.TopicMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;


/**
 * Unit tests for TopicServiceImpl.
 *
 * @author Zalyaletdinova Ilmira
 * @see TopicService
 * @see TopicServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for TopicServiceImpl")
public class TopicServiceImplTest {
    private final String string = "";
    private TopicService underTest;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TopicMapper topicMapper;

    @BeforeEach
    void setUp() {
        underTest = new TopicServiceImpl(topicRepository,topicMapper);
    }

    @Test
    @DisplayName(string)
    void findAll() {
        // given
        // when
        underTest.findAll();
        // then
        verify(topicRepository).findAll();
    }
}
