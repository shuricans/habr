package no.war.habr.controller;

import no.war.habr.persist.model.*;
import no.war.habr.persist.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for PictureController
 * Here we are testing the endpoint with a real file stored under resources.
 * path: src/test/resources/sad_pepe.png
 *
 * @author Karachev Sasha
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for PictureController")
class PictureControllerTest {

    @Autowired
    private MockMvc mvc;

    @Value("${app.picture.storage.path}")
    private String storagePath;

    public static final String FILE_NAME = "sad_pepe.png";

    @BeforeAll
    static void init(@Autowired UserRepository userRepository,
                     @Autowired RoleRepository roleRepository,
                     @Autowired TopicRepository topicRepository,
                     @Autowired PostRepository postRepository,
                     @Autowired PictureRepository pictureRepository) {
        // create and persist ROLE_USER
        Role roleUser = Role.builder()
                .name(ERole.ROLE_USER)
                .build();
        roleUser = roleRepository.save(roleUser);

        // create and persist user
        User user = User.builder()
                .username("username")
                .firstName("firstName")
                .password("password")
                .roles(Set.of(roleUser))
                .build();
        userRepository.save(user);

        // create and persist topic
        Topic topic = Topic.builder().name("Design").build();
        topic = topicRepository.save(topic);

        // create and persist post
        Post post = Post.builder()
                .title("First post")
                .content("Content")
                .description("Description")
                .topic(topic)
                .owner(user)
                .build();

        post = postRepository.save(post);

        // create picture in db
        Picture picture = Picture.builder()
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .post(post)
                .storageFileName(FILE_NAME)
                .name(FILE_NAME)
                .build();

        pictureRepository.save(picture);
    }

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository,
                         @Autowired RoleRepository roleRepository,
                         @Autowired TopicRepository topicRepository,
                         @Autowired PostRepository postRepository,
                         @Autowired PictureRepository pictureRepository) {
        pictureRepository.deleteAll();
        postRepository.deleteAll();
        topicRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("Should Download Picture")
    void shouldDownloadPicture() throws Exception {

        byte[] expectedBytes = Files.readAllBytes(Path.of(storagePath, FILE_NAME));

        MockHttpServletRequestBuilder getPictureRequest = MockMvcRequestBuilders
                .get("/picture/1")
                .contentType(MediaType.IMAGE_PNG);

        mvc.perform(getPictureRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(expectedBytes));
    }
}