package no.war.habr.controller;

import no.war.habr.auth.AuthCreator;
import no.war.habr.persist.model.*;
import no.war.habr.persist.repository.PostRepository;
import no.war.habr.persist.repository.RoleRepository;
import no.war.habr.persist.repository.TopicRepository;
import no.war.habr.persist.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for PostController
 *
 * @author Karachev Sasha
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for PostController")
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void init(@Autowired UserRepository userRepository,
                     @Autowired RoleRepository roleRepository,
                     @Autowired PasswordEncoder passwordEncoder,
                     @Autowired TopicRepository topicRepository,
                     @Autowired PostRepository postRepository) {

        // create and persist ROLE_USER
        Role roleUser = Role.builder()
                .name(ERole.ROLE_USER)
                .build();
        roleUser = roleRepository.save(roleUser);

        // create and persist user
        User user = User.builder()
                .username(AuthCreator.USERNAME)
                .firstName(AuthCreator.FIRSTNAME)
                .password(passwordEncoder.encode(AuthCreator.PASSWORD))
                .roles(Set.of(roleUser))
                .build();
        userRepository.save(user);

        // create and persist topic
        Topic topic = Topic.builder().name("Design").build();
        topic = topicRepository.save(topic);

        // create and persist post #1
        Post firstPost = Post.builder()
                .title("First post")
                .content("Content")
                .description("Description")
                .topic(topic)
                .owner(user)
                .build();

        postRepository.save(firstPost);

        // create and persist post #2
        Post secondPost = Post.builder()
                .title("Second post")
                .content("Content_2")
                .description("Description_2")
                .topic(topic)
                .owner(user)
                .build();

        postRepository.save(secondPost);
    }

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository,
                         @Autowired RoleRepository roleRepository,
                         @Autowired TopicRepository topicRepository,
                         @Autowired PostRepository postRepository) {
        postRepository.deleteAll();
        topicRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("listAll Returns List Of Posts Inside Page Object When Successful")
    void listAll_ReturnsListOfPostsInsidePageObject_WhenSuccessful() throws Exception {
        MockHttpServletRequestBuilder listAllPostsRequest = MockMvcRequestBuilders
                .get("/posts")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(listAllPostsRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(2)))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.last").exists())
                .andExpect(jsonPath("$.sort").exists())
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(2)))
                .andExpect(jsonPath("$.empty", is(false)));
    }

    @Test
    @DisplayName("findById Returns Post By Id When Successful")
    void findById_ReturnsPostById_WhenSuccessful() throws Exception {
        MockHttpServletRequestBuilder listAllPostsRequest = MockMvcRequestBuilders
                .get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(listAllPostsRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("First post")))
                .andExpect(jsonPath("$.content", is("Content")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.topic", is("Design")))
                .andExpect(jsonPath("$.owner", is(AuthCreator.USERNAME)));
    }

    @Test
    @DisplayName("findById Returns 404 When Post Does Not Exist")
    void findById_Returns404_WhenPostDoesNotExist() throws Exception {
        MockHttpServletRequestBuilder listAllPostsRequest = MockMvcRequestBuilders
                .get("/posts/14")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(listAllPostsRequest)
                .andExpect(status().isNotFound());
    }
}