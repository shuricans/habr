package no.war.habr.controller;


import no.war.habr.persist.model.Topic;
import no.war.habr.persist.repository.TopicRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for TopicController
 *
 * @author Zalyaletdinova Ilmira
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for TopicController")
public class TopicControllerTest {
    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void init(@Autowired TopicRepository topicRepository) {
        // create topics
        Topic designTopic = Topic.builder().name("Design").build();
        Topic webTopic = Topic.builder().name("Web").build();

        topicRepository.saveAll(List.of(designTopic, webTopic));
    }

    @AfterAll
    static void afterAll(@Autowired TopicRepository topicRepository) {
        // delete all topics
        topicRepository.deleteAll();
    }

    @Test
    @DisplayName("FindAll Returns List Of Topics When Successful")
    public void FindAllReturnsListOfTopicsWhenSuccessful() throws Exception {

        MockHttpServletRequestBuilder getAllTopicsRequest = MockMvcRequestBuilders
                .get("/topics")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(getAllTopicsRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }
}
