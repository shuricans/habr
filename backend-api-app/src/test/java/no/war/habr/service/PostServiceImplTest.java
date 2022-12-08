package no.war.habr.service;

import no.war.habr.exception.*;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static no.war.habr.util.user.UserCreator.*;
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

    public static final String POST_NOT_FOUND_TEMPLATE = "Post with id [%d] not found.";
    public static final String USER_NOT_FOUND_TEMPLATE = "User [%s] not found.";
    public static final String USER_NOT_ACTIVE_TEMPLATE = "User [%s] is not active.";
    public static final String NOT_OWNER_TEMPLATE = "You are not the owner of this post!";
    public static final String TOPIC_NOT_FOUND_TEMPLATE = "Topic [%s] does not exist.";
    public static final String POST_DELETED_ALREADY_TEMPLATE = "Post with id [%d] has already been deleted";
    public static final String POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE = "Post with id [%d] %s successfully";
    public static final String USER_MUST_BE_MOD_OR_ADM = "User [%s] must be ADMIN or MODERATOR";

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
    private PictureService pictureService;

    @Mock
    private PostMapper postMapper;


    @BeforeEach
    void setUp() {
        underTest = new PostServiceImpl(
                postRepository,
                topicRepository,
                tagRepository,
                userRepository,
                postMapper,
                pictureService);

        ReflectionTestUtils.setField(underTest, "defaultSortDirection", "ASC");
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
                .hasMessageContaining(USER_NOT_FOUND_TEMPLATE, nonExistentUsername);
    }

    @Test
    @DisplayName("save Should Throw ForbiddenException When User Not Active")
    void save_ShouldThrowForbiddenException_WhenUserNotActive() {
        // given
        User user = createUser();
        user.setCondition(EUserCondition.DELETED);
        String username = user.getUsername();
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.save(username, any(PostDataRequest.class)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(USER_NOT_ACTIVE_TEMPLATE, username);
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
                .hasMessageContaining(TOPIC_NOT_FOUND_TEMPLATE, nonExistentTopic);
    }

    @Test
    @DisplayName("save Should Throw ForbiddenException When Wrong Owner")
    void save_ShouldThrowForbiddenException_WhenWrongOwner() {
        // given
        User user = createUser();
        long postId = 1L;
        PostDataRequest postDataRequest = PostDataRequest.builder()
                .postId(postId)
                .topic("")
                .build();
        Post post = Post.builder()
                .id(postId)
                .owner(User.builder().id(42L).build())
                .build();
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(topicRepository.findByName(anyString())).willReturn(Optional.of(Topic.builder().build()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        // then
        assertThatThrownBy(() -> underTest.save(user.getUsername(), postDataRequest))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(NOT_OWNER_TEMPLATE);
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


    /**
     * delete should throw {@code UserNotFoundException} when user not exist
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("delete Should Throw UserNotFoundException When User Not Exist")
    void delete_ShouldThrowUserNotFoundException_WhenUserNotExist() {
        // given
        String username = "username";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(username, anyLong()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND_TEMPLATE, username);
    }


    /**
     * delete should Throw {@code ForbiddenException} when user not active
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("delete Should Throw ForbiddenException When User Not Active")
    void delete_ShouldThrowForbiddenException_WhenUserNotActive() {
        // given
        User owner = createUser();
        owner.setCondition(EUserCondition.NOT_ACTIVE);
        String username = owner.getUsername();
        given(userRepository.findByUsername(username)).willReturn(Optional.of(owner));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(username, anyLong()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(USER_NOT_ACTIVE_TEMPLATE, username);
    }


    /**
     * delete should throw {@code PostNotFoundException} when post not found
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("delete Should Throw PostNotFoundException When Post Not Found")
    void delete_ShouldThrowPostNotFoundException_WhenPostNotFound() {
        // given
        User owner = createUser();
        long postId = 1;
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(owner));
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(anyString(), postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining(POST_NOT_FOUND_TEMPLATE, postId);
    }


    /**
     * delete should Throw Precondition Failed Exception when user not ownerPost
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("delete Should Throw Precondition Failed Exception When User Not OwnerPost")
    void delete_ShouldThrowPreconditionFailedException_WhenUserNotOwnerPost() {
        // given
        User user = createUser();
        User owner = createUser();
        owner.setId(2L);
        Post post = Post.builder()
                .owner(owner)
                .build();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(user.getUsername(), anyLong()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(NOT_OWNER_TEMPLATE);
    }


    /**
     * delete should Throw {@code ForbiddenException} when post deleted already
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("delete Should Throw ForbiddenException When Post Deleted Already")
    void delete_ShouldThrowForbiddenException_WhenPostDeletedAlready() {
        // given

        User owner = createUser();
        long postId = 2L;
        Post post = Post.builder()
                .id(postId)
                .owner(owner)
                .condition(EPostCondition.DELETED)
                .build();
        String username = owner.getUsername();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(owner));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(username, postId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(POST_DELETED_ALREADY_TEMPLATE, postId);

    }


    /**
     * Unit tests for PostServiceImpl.
     * delete by id should change post condition to deleted when successful
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("deleteById_ShouldChangePostConditionToDeleted_WhenSuccessful")
    void deleteById_ShouldChangePostConditionToDeleted_WhenSuccessful() {
        // given
        long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .build();
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        String expectedMessage = String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "deleted");

        // when
        MessageResponse messageResponse = underTest.deleteById(postId);

        // then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();

        assertThat(capturedPost).isEqualTo(post);
        assertThat(capturedPost.getCondition()).isEqualTo(EPostCondition.DELETED);
        assertThat(messageResponse.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("publish Should Throw UserNotFoundException When User Not Exist")
    public void  publish_ShouldThrowUserNotFoundException_WhenUserNotExist() {
        //given
        String username = "username";

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, anyLong()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND_TEMPLATE, username);
    }

    @Test
    @DisplayName("publish Should Throw ForbiddenException When User Not Active")
    public void publish_ShouldThrowForbiddenException_WhenUserNotActive() {
        //given
        User user = createUser();
        user.setCondition(EUserCondition.NOT_ACTIVE);
        String username = user.getUsername();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, anyLong()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(USER_NOT_ACTIVE_TEMPLATE, username);
    }

    @Test
    @DisplayName("publish Should Throw PostNotFoundException When Post Not Found")
    public void publish_ShouldThrowPostNotFoundException_WhenPostNotFound() {
        //given
        User user = createUser();
        String username = user.getUsername();
        long postId = 1123L;

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining(POST_NOT_FOUND_TEMPLATE, postId);
    }

    @Test
    @DisplayName("publish Should Throw ForbiddenException When User Not Owner Of Post")
    public void publish_ShouldThrowForbiddenException_WhenUserNotOwnerOfPost() {
        //given
        User user = createUser();
        User owner = User.builder()
                .id(2L)
                .build();

        String username = user.getUsername();

        Post post = Post.builder()
                .id(11L)
                .owner(owner)
                .build();

        long postId = post.getId();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, postId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(NOT_OWNER_TEMPLATE);
    }

    @Test
    @DisplayName("publish Should Throw BadRequestException When Post Has Invalid Condition")
    public void publish_ShouldThrowBadRequestException_WhenPostHasInvalidCondition() {
        //given
        User user = createUser();
        String username = user.getUsername();
        Post post = Post.builder()
                .id(11L)
                .owner(user)
                .condition(EPostCondition.DELETED)
                .build();

        long postId = post.getId();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        //when
        //then
        assertThatThrownBy(() -> underTest.publish(username, postId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Post with id [%d] must be DRAFT or HIDDEN", postId);
    }

    @Test
    @DisplayName("publish Should Change Post Condition To PUBLISHED When Successful")
    public void publish_ShouldChangePostConditionToPUBLISHED_WhenSuccessful() {
        //given
        User user = createUser();
        Post post = Post.builder()
                .id(11L)
                .condition(EPostCondition.DRAFT)
                .owner(user)
                .build();

        String username = user.getUsername();
        long postId = post.getId();
        String expectedMessageResponse = String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "published");

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        //when
        MessageResponse messageResponse = underTest.publish(username, postId);

        //then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();

        assertThat(capturedPost).isEqualTo(post);
        assertThat(capturedPost.getCondition()).isEqualTo(EPostCondition.PUBLISHED);
        assertThat(messageResponse.getMessage()).isEqualTo(expectedMessageResponse);
    }

    @Test
    @DisplayName("ban Should Throw UserNotFoundException When User Not Exist")
    public void ban_ShouldThrowUserNotFoundException_WhenUserNotExist() {
        //given
        String username = "username";

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.ban(username, anyLong()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND_TEMPLATE, username);
    }

    @Test
    @DisplayName("ban Should Throw ForbiddenException When User Not Active")
    public void ban_ShouldThrowForbiddenException_WhenUserNotActive() {
        //given
        User user = createUser();
        user.setCondition(EUserCondition.NOT_ACTIVE);
        String username = user.getUsername();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        //when
        //then
        assertThatThrownBy(() -> underTest.ban(username, anyLong()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(USER_NOT_ACTIVE_TEMPLATE, username);
    }

    @Test
    @DisplayName("ban Should Throw ForbiddenException When User Not ADMIN or MODERATOR")
    public void ban_ShouldThrowForbiddenException_WhenUserNotADMINorMODERATOR() {
        //given
        User user = createUser();
        user.setCondition(EUserCondition.ACTIVE);
        Set<Role> roleAdmin = Set.of(Role.builder().name(ERole.ROLE_USER).build());
        user.setRoles(roleAdmin);
        String username = user.getUsername();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        //when
        //then
        assertThatThrownBy(() -> underTest.ban(username, anyLong()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(USER_MUST_BE_MOD_OR_ADM, username);
    }

    @Test
    @DisplayName("ban Should Throw PostNotFoundException When Post Not Found")
    public void ban_ShouldThrowPostNotFoundException_WhenPostNotFound() {
        //given
        User user = createModerator();
        String username = user.getUsername();
        long postId = 1123L;

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.ban(username, postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining(POST_NOT_FOUND_TEMPLATE, postId);
    }

    @Test
    @DisplayName("ban Should Throw ForbiddenException When Post Not Published")
    public void ban_ShouldThrowForbiddenException_WhenPostNotNotPublished() {
        //given
        User user = createModerator();
        String username = user.getUsername();
        Post post = Post.builder()
                .id(1111L)
                .condition(EPostCondition.DRAFT)
                .build();

        long postId = post.getId();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        //when
        //then
        assertThatThrownBy(() -> underTest.ban(username, postId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Post with id [%d] is not yet PUBLISHED", postId);
    }

    @Test
    @DisplayName("ban Should Should Change Post Condition To BANNED When Successful")
    public void ban_ShouldChangePostConditionToBANNED_WhenSuccessful() {
        //given
        User user = createAdmin();
        String username = user.getUsername();
        Post post = Post.builder()
                .id(1111L)
                .condition(EPostCondition.PUBLISHED)
                .build();

        long postId = post.getId();
        String expectedMessageResponse = String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "banned");

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        //when
        MessageResponse messageResponse = underTest.ban(username, postId);
        //then
        ArgumentCaptor<Post> argumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(argumentCaptor.capture());
        Post capturedPost = argumentCaptor.getValue();

        assertThat(capturedPost).isEqualTo(post);
        assertThat(capturedPost.getCondition()).isEqualTo(EPostCondition.BANNED);
        assertThat(messageResponse.getMessage()).isEqualTo(expectedMessageResponse);
    }

    /**
     * Unit tests for PostServiceImpl.
     * deleteAny should throw user not found Exception when user not existent
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("deleteAny Should Throw UserNotFoundException When User Not Exist")
    void deleteAny_ShouldThrowUserNotFoundException_WhenUserNotExist() {
        //given
        String username = "username";

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.deleteAny(username, anyLong()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND_TEMPLATE, username);
    }

    /**
     * Unit tests for PostServiceImpl.
     * deleteAny should Throw Forbidden Exception  when user not active
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("deleteAny Should Throw For ForbiddenException When User Not Active")
    void deleteAny_ShouldThrowForbiddenException_WhenUserNotActive() {
        //given
        User user = createUser();
        user.setCondition(EUserCondition.NOT_ACTIVE);
        String username = user.getUsername();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        //when
        //then
        assertThatThrownBy(() -> underTest.deleteAny(username, anyLong()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(USER_NOT_ACTIVE_TEMPLATE, username);
    }

    /**
     * Unit tests for PostServiceImpl.
     * deleteAny should throw forbidden exception when user not admin and not moderator
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("deleteAny Should Throw ForbiddenException When User Not Admin And Not Moderator")
    void deleteAny_ShouldThrowForbiddenException_WhenUserNotAdminAndNotModerator(){
        //given
        User user = createUser();
        String username = user.getUsername();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        //when
        //then
        assertThatThrownBy(() -> underTest.deleteAny(username, anyLong()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(String.format(USER_MUST_BE_MOD_OR_ADM, username));

    }


    /**
     * Unit tests for PostServiceImpl.
     * deleteAny should throw post not found Exception when post not found
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("deleteAny Should Throw Post Not Found Exception When Post Not Found")
    void deleteAny_ShouldThrowPostNotFoundException_WhenPostNotFound() {
        //given
        User user = createModerator();
        String username = user.getUsername();
        long postId = 1L;

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.deleteAny(username, postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining(POST_NOT_FOUND_TEMPLATE, postId);
    }

    /**
     * Unit tests for PostServiceImpl.
     * deleteAny should change post condition to banned when successful
     *
     * @author Zalyaletdinova Ilmira
     * @see PostServiceImpl
     */
    @Test
    @DisplayName("deleteAny Should Change Post Condition To Banned When Successful")
    public void deleteAny_ShouldChangePostConditionToBanned_WhenSuccessful() {
        //given
        User user = createAdmin();
        String username = user.getUsername();

        long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .build();
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        String expectedMessage = (String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "delete"));

        // when
        MessageResponse messageResponse = underTest.deleteAny(username, postId);

        // then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());
        Post capturedPost = postArgumentCaptor.getValue();

        assertThat(capturedPost).isEqualTo(post);
        assertThat(capturedPost.getCondition()).isEqualTo(EPostCondition.BANNED);
        assertThat(messageResponse.getMessage()).isEqualTo(expectedMessage);
    }
}


