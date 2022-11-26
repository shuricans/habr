package no.war.habr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.war.habr.auth.AuthCreator;
import no.war.habr.payload.request.PostDataRequest;
import no.war.habr.payload.response.JwtResponse;
import no.war.habr.persist.model.*;
import no.war.habr.persist.repository.*;
import no.war.habr.persist.specification.PostSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static no.war.habr.util.signin.AuthenticationUtils.signin;
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

    public static final String DESIGN = "Design";
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    private static Post firstPost;

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
        Topic topic = Topic.builder().name(DESIGN).build();
        topic = topicRepository.save(topic);

        // create tags
        Tag tag_1 = Tag.builder().name("tag_1").build();
        Tag tag_2 = Tag.builder().name("tag_2").build();
        Tag tag_3 = Tag.builder().name("tag_3").build();

        // create and persist post #1
        firstPost = Post.builder()
                .title("First post")
                .content("Content")
                .description("Description")
                .topic(topic)
                .owner(user)
                .tags(Set.of(tag_1, tag_2, tag_3))
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

        // create and persist post #3 - PUBLISHED
        Post thirdPost = Post.builder()
                .title("Third post")
                .content("Content_3")
                .description("Description_3")
                .condition(EPostCondition.PUBLISHED)
                .topic(topic)
                .owner(user)
                .build();

        postRepository.save(thirdPost);

        // create and persist post #4 - PUBLISHED
        Post fourthPost = Post.builder()
                .title("Fourth post")
                .content("Content_4")
                .description("Description_4")
                .condition(EPostCondition.PUBLISHED)
                .topic(topic)
                .owner(user)
                .build();

        postRepository.save(fourthPost);
    }

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository,
                         @Autowired RoleRepository roleRepository,
                         @Autowired TopicRepository topicRepository,
                         @Autowired PostRepository postRepository,
                         @Autowired RefreshTokenRepository refreshTokenRepository) {
        postRepository.deleteAll();
        topicRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("listAll Returns List Of Published Posts Inside Page Object When Successful")
    void listAll_ReturnsListOfPublishedPostsInsidePageObject_WhenSuccessful() throws Exception {

        Specification<Post> spec = Specification
                .where(PostSpecification.condition(EPostCondition.PUBLISHED));

        List<Post> publishedPosts = postRepository.findAll(spec);

        int count = publishedPosts.size();

        MockHttpServletRequestBuilder listAllPostsRequest = MockMvcRequestBuilders
                .get("/posts")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(listAllPostsRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(count)))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements", is(count)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.last").exists())
                .andExpect(jsonPath("$.sort").exists())
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(count)))
                .andExpect(jsonPath("$.empty", is(false)));
    }

    @Test
    @DisplayName("findById Returns Only Published Post By Id When Successful")
    void findById_ReturnsOnlyPublishedPostById_WhenSuccessful() throws Exception {

        Specification<Post> spec = Specification
                .where(PostSpecification.condition(EPostCondition.PUBLISHED));

        List<Post> publishedPosts = postRepository.findAll(spec);
        Post post = publishedPosts.get(0);
        long postId = post.getId();

        MockHttpServletRequestBuilder listAllPostsRequest = MockMvcRequestBuilders
                .get("/posts/" + postId)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(listAllPostsRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int)postId)))
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.content", is(post.getContent())))
                .andExpect(jsonPath("$.description", is(post.getDescription())))
                .andExpect(jsonPath("$.topic", is(post.getTopic().getName())))
                .andExpect(jsonPath("$.owner", is(post.getOwner().getUsername())));
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

    @Test
    @DisplayName("save Should Save New Post When Successful")
    void save_ShouldSaveNewPost_WhenSuccessful() throws Exception {
        String token = getAccessToken(AuthCreator.USERNAME, AuthCreator.PASSWORD);

        String title = "Awesome post title";
        String content = "content";
        String description = "description";
        String tag_1 = "#tag_1";
        String tag_2 = "#tag_2";
        PostDataRequest postDataRequest = PostDataRequest.builder()
                .title(title)
                .content(content)
                .description(description)
                .tags(Set.of(tag_1, tag_2))
                .topic(DESIGN)
                .build();

        String jsonPostDataRequest = objectMapper.writeValueAsString(postDataRequest);

        MockHttpServletRequestBuilder savePostRequest = MockMvcRequestBuilders
                .post("/posts/save")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonPostDataRequest);

        mvc.perform(savePostRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.content", is(content)))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.condition", is(EPostCondition.DRAFT.name())))
                .andExpect(jsonPath("$.owner", is(AuthCreator.USERNAME)))
                .andExpect(jsonPath("$.topic", is(DESIGN)))
                .andExpect(jsonPath("$.tags[0]", is(tag_1)))
                .andExpect(jsonPath("$.tags[1]", is(tag_2)));
    }

    @Test
    @DisplayName("save Should Update Existent Post When Successful")
    void save_ShouldUpdateExistentPost_WhenSuccessful() throws Exception {
        String token = getAccessToken(AuthCreator.USERNAME, AuthCreator.PASSWORD);

        String newContent = "New content";
        String newDescription = "New description";
        Set<String> newTags = firstPost.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        newTags.add("new_tag");

        PostDataRequest postDataRequest = PostDataRequest.builder()
                .postId(firstPost.getId())
                .title(firstPost.getTitle())
                .content(newContent)
                .description(newDescription)
                .topic(firstPost.getTopic().getName())
                .tags(newTags)
                .build();

        String jsonPostDataRequest = objectMapper.writeValueAsString(postDataRequest);

        MockHttpServletRequestBuilder updatePostRequest = MockMvcRequestBuilders
                .post("/posts/save")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonPostDataRequest);

        mvc.perform(updatePostRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is(firstPost.getTitle())))
                .andExpect(jsonPath("$.content", is(newContent)))
                .andExpect(jsonPath("$.description", is(newDescription)))
                .andExpect(jsonPath("$.condition", is(firstPost.getCondition().name())))
                .andExpect(jsonPath("$.owner", is(firstPost.getOwner().getUsername())))
                .andExpect(jsonPath("$.topic", is(firstPost.getTopic().getName())))
                .andExpect(jsonPath("$.tags.length()", is(4)));
    }

    @Test
    @DisplayName("random Should Return Random Published Post When Successful")
    void random_ShouldReturnRandomPublishedPost_WhenSuccessful() throws Exception {
        MockHttpServletRequestBuilder getRandomPostRequest = MockMvcRequestBuilders
                .get("/posts/random")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(getRandomPostRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.condition", is(EPostCondition.PUBLISHED.name())));
    }

    @Test
    @DisplayName("listOwnPosts Returns List Of Own Posts Except DELETED Inside Page Object When Successful")
    void listOwnPosts_ReturnsListOfOwnPostsExceptDELETEDInsidePageObject_WhenSuccessful() throws Exception {
        String token = getAccessToken(AuthCreator.USERNAME, AuthCreator.PASSWORD);

        Specification<Post> spec = Specification
                .where(PostSpecification.excludeCondition(EPostCondition.DELETED));

        List<Post> postsExceptDeleted = postRepository.findAll(spec);

        int size = postsExceptDeleted.size();

        MockHttpServletRequestBuilder getOwnPostRequest = MockMvcRequestBuilders
                .get("/posts/own")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(getOwnPostRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(size)))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements", is(size)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.last").exists())
                .andExpect(jsonPath("$.sort").exists())
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(size)))
                .andExpect(jsonPath("$.empty", is(false)));
    }

    @Test
    @DisplayName("listOwnPosts Returns 401 Unauthorized When Token Not Provided")
    void listOwnPosts_Returns401Unauthorized_WhenTokenNotProvided() throws Exception {
        MockHttpServletRequestBuilder getOwnPostRequest = MockMvcRequestBuilders
                .get("/posts/own")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(getOwnPostRequest)
                .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    private String getAccessToken(String username, String password) {
        JwtResponse jwtResponse = signin(
                username,
                password,
                objectMapper,
                mvc);
        assert jwtResponse != null;
        return jwtResponse.getToken();
    }
}