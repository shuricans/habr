package no.war.habr.service;

import no.war.habr.exception.BadRequestException;
import no.war.habr.exception.PostNotFoundException;
import no.war.habr.exception.TopicNotFoundException;
import no.war.habr.exception.UserNotFoundException;
import no.war.habr.payload.request.PostDataRequest;
import no.war.habr.payload.response.MessageResponse;
import no.war.habr.persist.model.*;
import no.war.habr.persist.repository.PostRepository;
import no.war.habr.persist.repository.TagRepository;
import no.war.habr.persist.repository.TopicRepository;
import no.war.habr.persist.repository.UserRepository;
import no.war.habr.service.dto.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static no.war.habr.util.user.UserCreator.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.by;

/**
 * Unit tests for PostServiceImpl.
 *
 * @author Karachev Sasha
 * @see PostService
 * @see PostServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for PostServiceImplT")
class PostServiceImplTest {

    private PostService underTest;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostMapper postMapper;

    @BeforeEach
    void setUp() {
        underTest = new PostServiceImpl(
                postRepository,
                topicRepository,
                tagRepository,
                userRepository,
                postMapper);

        ReflectionTestUtils.setField(underTest, "defaultSortDirection", "ASC");
    }

    @Test
    @DisplayName("find UserNotFoundException When user not found")
    public void  publish_itShouldUserNotFoundExceptionIfUserNotFound() {
        //given
        String username = "username";

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, anyLong()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with username: %s not found.", username);
    }

    @Test
    @DisplayName("find UserNotFoundException When user has no active conditions")
    public void publish_itShouldUserNotFoundExceptionIfUserNoActive() {
        //given
        User user = createUser();
        user.setCondition(EUserCondition.NOT_ACTIVE);
        String username = user.getUsername();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, anyLong()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with username: %s with EUserCondition.NOT_ACTIVE not found.", username);
    }

    @Test
    @DisplayName("find PostNotFoundException When post not found")
    public void publish_itShouldPostNotFoundExceptionWhenPostNotFound() {
        //given
        User user = createUser();
        user.setCondition(EUserCondition.ACTIVE);
        String username = user.getUsername();
        Post post = Post.builder()
                .id(11L)
                .build();

        long postId = post.getId();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with id: %d not found.", postId);
    }

    @Test
    @DisplayName("find PostNotFoundException When post not found")
    public void publish_itShouldPostNotFoundExceptionWhenPostHasEPostConditionDeleted() {
        //given
        User user = createUser();
        user.setCondition(EUserCondition.ACTIVE);
        String username = "user.getUsername()";
        Post post = Post.builder()
                .id(11L)
                .condition(EPostCondition.DELETED)
                .build();

        long postId = post.getId();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with postId: %d with EPostCondition: %s not found.", postId, post.getCondition());
    }

    @Test
    @DisplayName("find UserNotFoundException When user not have owner this post")
    public void publish_itShouldUserNotFoundExceptionWhenUserNotHavePostThisOwner() {
        //given
        User user = createUser();
        User owner = User.builder()
                .id(2L)
                .username("user")
                .condition(EUserCondition.ACTIVE)
                .build();

        user.setCondition(EUserCondition.ACTIVE);
        String username = user.getUsername();

        Post post = Post.builder()
                .id(11L)
                .condition(EPostCondition.HIDDEN)
                .owner(owner)
                .build();
        long postId = post.getId();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, postId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User is not the owner of this post.");
    }

    @Test
    @DisplayName("find MessageResponse Цhen user and post passed all checks and didn't throw exceptions")
    public void publish_itShouldMessageResponse() {
        //given
        User user = createUser();
        User owner = createUser();

        user.setCondition(EUserCondition.ACTIVE);
        String username = user.getUsername();
        Post post = Post.builder()
                .id(11L)
                .condition(EPostCondition.HIDDEN)
                .owner(owner)
                .build();

        long postId = post.getId();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        String expectedMessageResponse = String.format("Post with id = %d published successfully", postId);

        //when
        MessageResponse messageResponse = underTest.publish(username, postId);

        //then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();

        assertThat(capturedPost).isEqualTo(post);
        assertThat(capturedPost.getCondition()).isEqualTo(post.getCondition());
        assertThat(capturedPost.getOwner()).isEqualTo(post.getOwner());

        assertThat(messageResponse.getMessage()).isEqualTo(expectedMessageResponse);

    }

    @Test
    @DisplayName("findAll Returns List Of PostDto Inside Page Object When Successful")
    void findAll_ReturnsListOfPostDtoInsidePageObject_WhenSuccessful() {
        // given
        Optional<String> optionalTopic = Optional.of("topic");
        Optional<String> optionalUsername = Optional.of("username");
        Optional<String> optionalTag = Optional.of("tag");
        Optional<String> optionalCondition = Optional.of("DRAFT");
        Optional<String> excludedCondition = Optional.of("deleted");

        int page = 1;
        int size = 10;
        String sortField = "id";
        Direction direction = Direction.ASC;

        PageRequest expectedPageRequest = PageRequest.of(page - 1, size, by(direction, sortField));

        @SuppressWarnings("unchecked")
        Page<Post> mockPage = (Page<Post>) mock(Page.class);

        when(postRepository.findAll(ArgumentMatchers.<Specification<Post>>any(), any(PageRequest.class)))
                .thenReturn(mockPage);
        // when
        underTest.findAll(optionalUsername,
                optionalTopic,
                optionalTag,
                optionalCondition,
                excludedCondition,
                Optional.of(page),
                Optional.of(size),
                Optional.of(sortField),
                Optional.of(direction));

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Specification<Post>> specificationArgumentCaptor =
                ArgumentCaptor.forClass(Specification.class);

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor =
                ArgumentCaptor.forClass(PageRequest.class);

        verify(postRepository).findAll(specificationArgumentCaptor.capture(),
                pageRequestArgumentCaptor.capture());

        Specification<Post> capturedSpecification = specificationArgumentCaptor.getValue();
        PageRequest capturedPageRequest = pageRequestArgumentCaptor.getValue();

        assertThat(capturedPageRequest).isEqualTo(expectedPageRequest);
        assertThat(capturedSpecification).isInstanceOf(Specification.class);
    }

    @Test
    @DisplayName("findById Returns Published PostDto By Id When Successful")
    void findById_ReturnsPublishedPostDtoById_WhenSuccessful() {
        // given
        // when
        underTest.findById(anyLong());

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Specification<Post>> specificationArgumentCaptor =
                ArgumentCaptor.forClass(Specification.class);

        verify(postRepository).findOne(specificationArgumentCaptor.capture());

        Specification<Post> capturedSpecification = specificationArgumentCaptor.getValue();

        assertThat(capturedSpecification).isInstanceOf(Specification.class);
    }

    @Test
    @DisplayName("save Should Throw UserNotFoundException When User Does Not Exist")
    void save_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // given
        String nonExistentUsername = "username";
        PostDataRequest postDataRequest = PostDataRequest.builder().build();
        given(userRepository.findByUsername(nonExistentUsername)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> underTest.save(nonExistentUsername, postDataRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with username = %s not found.", nonExistentUsername);
    }

    @Test
    @DisplayName("save Should Throw TopicNotFoundException When Topic Does Not Exist")
    void save_ShouldThrowTopicNotFoundException_WhenTopicDoesNotExist() {
        // given
        User user = createUser();
        String nonExistentTopic = "nonExistentTopic";
        PostDataRequest postDataRequest = PostDataRequest.builder()
                .topic(nonExistentTopic)
                .build();
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(topicRepository.findByName(nonExistentTopic)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.save(user.getUsername(), postDataRequest))
                .isInstanceOf(TopicNotFoundException.class)
                .hasMessageContaining("Topic by name = %s does not exist.", nonExistentTopic);
    }

    @Test
    @DisplayName("save Should Throw BadRequestException When Wrong Owner")
    void save_ShouldThrowBadRequestException_WhenWrongOwner() {
        // given
        User user = createUser();
        Topic topic = Topic.builder().name("topic").build();
        Set<String> tags = Set.of("tag_1", "tag_2");
        String title = "title";
        String content = "content";
        String description = "description";
        long postId = 1L;
        PostDataRequest postDataRequest = PostDataRequest.builder()
                .postId(postId)
                .title(title)
                .content(content)
                .description(description)
                .topic(topic.getName())
                .tags(tags)
                .build();
        Post post = Post.builder()
                .id(postId)
                .owner(User.builder().id(42L).build())
                .build();
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(topicRepository.findByName(anyString())).willReturn(Optional.of(topic));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        // then
        assertThatThrownBy(() -> underTest.save(user.getUsername(), postDataRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("You are not the owner of this post!");
    }

    @Test
    @DisplayName("save Should Save Post When Successful")
    void save_ShouldSavePost_WhenSuccessful() {
        // given
        User user = createUser();
        Topic topic = Topic.builder().name("topic").build();
        Set<String> tags = Set.of("tag_1", "tag_2");
        String title = "title";
        String content = "content";
        String description = "description";

        PostDataRequest postDataRequest = PostDataRequest.builder()
                .title(title)
                .content(content)
                .description(description)
                .topic(topic.getName())
                .tags(tags)
                .build();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(topicRepository.findByName(anyString())).willReturn(Optional.of(topic));
        given(tagRepository.findByName(anyString())).willReturn(Optional.empty());

        // when
        underTest.save(user.getUsername(), postDataRequest);

        // then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();

        assertThat(capturedPost).isNotNull();
        assertThat(capturedPost.getTitle()).isEqualTo(title);
        assertThat(capturedPost.getContent()).isEqualTo(content);
        assertThat(capturedPost.getDescription()).isEqualTo(description);
        assertThat(capturedPost.getTopic().getName()).isEqualTo(topic.getName());
        assertThat(capturedPost.getOwner()).isEqualTo(user);
    }
}