package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.exception.*;
import no.war.habr.payload.request.PostDataRequest;
import no.war.habr.payload.response.MessageResponse;
import no.war.habr.persist.model.*;
import no.war.habr.persist.repository.*;
import no.war.habr.persist.specification.PostSpecification;
import no.war.habr.service.dto.PostDto;
import no.war.habr.service.dto.PostMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static no.war.habr.persist.model.ERole.*;
import static no.war.habr.util.SpecificationUtils.combineSpec;

/**
 * Implementation of the PostService interface.
 *
 * @author Karachev Sasha
 * @see PostService
 */
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    public static final String POST_NOT_FOUND_TEMPLATE = "Post with id [%d] not found.";
    public static final String USER_NOT_FOUND_TEMPLATE = "User [%s] not found.";
    public static final String USER_NOT_ACTIVE_TEMPLATE = "User [%s] is not active.";
    public static final String NOT_OWNER_TEMPLATE = "You are not the owner of this post!";
    public static final String TOPIC_NOT_FOUND_TEMPLATE = "Topic [%s] does not exist.";
    public static final String POST_DELETED_ALREADY_TEMPLATE = "Post with id [%d] has already been deleted";
    public static final String POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE = "Post with id [%d] %s successfully";
    public static final String USER_MUST_BE_MOD_OR_ADM = "User [%s] must be ADMIN or MODERATOR";

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final PictureService pictureService;

    @Value("${app.defaultSizePerPage.posts}")
    private int defaultSizePerPage;

    @Value("${app.defaultSortField.posts}")
    private String defaultSortField;

    @Value("${app.defaultSortDirection.posts}")
    private String defaultSortDirection;

    /**
     * Fetch all {@code Post} by filter
     *
     * @param  username {@code Optional&lt;String&gt;} user "nickname"
     * @param  topic {@code Optional&lt;String&gt;} topic name
     * @param  tag {@code Optional&lt;String&gt;} tag name
     * @param  condition {@code Optional&lt;String&gt;} post state ({@code EPostCondition})
     * @param  excludeCondition {@code Optional&lt;String&gt;} post state ({@code EPostCondition})
     * @param  page {@code Optional&lt;Integer&gt;} page number
     * @param  size {@code Optional&lt;Integer&gt;} page size
     * @param  sortField {@code Optional&lt;String&gt;} sort field
     * @param  direction {@code Optional&lt;Direction&gt;} ASC/DESC sort direction
     * @return {@code Page&lt;PostDto&gt;}
     *
     * @throws BadRequestException when provided
     *         {@code Optional&lt;String&gt; condition} or
     *         {@code Optional&lt;String&gt; excludeCondition} are invalid
     */
    @Override
    public Page<PostDto> findAll(Optional<String> username,
                                 Optional<String> topic,
                                 Optional<String> tag,
                                 Optional<String> condition,
                                 Optional<String> excludeCondition,
                                 Optional<Integer> page,
                                 Optional<Integer> size,
                                 Optional<String> sortField,
                                 Optional<Direction> direction) {
        Specification<Post> spec = null;

        if (topic.isPresent() && !topic.get().isBlank()) {
            spec = Specification.where(PostSpecification.topic(topic.get()));
        }

        if (tag.isPresent() && !tag.get().isBlank()) {
            spec = combineSpec(spec, PostSpecification.hasTags(List.of(tag.get())));
        }

        if (username.isPresent() && !username.get().isBlank()) {
            spec = combineSpec(spec, PostSpecification.username(username.get()));
        }

        try {
            if (condition.isPresent()) {
                spec = combineSpec(spec,
                        PostSpecification
                                .condition(EPostCondition.valueOf(condition.get().toUpperCase())));
            }
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(exception.getMessage());
        }

        try {
            if (excludeCondition.isPresent()) {
                spec = combineSpec(spec,
                        PostSpecification
                                .excludeCondition(EPostCondition.valueOf(excludeCondition.get().toUpperCase())));
            }
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(exception.getMessage());
        }

        spec = combineSpec(spec, Specification.where(null));

        String sortBy;

        if (sortField.isPresent() && !sortField.get().isEmpty()) {
            sortBy = sortField.get();
        } else {
            sortBy = defaultSortField;
        }

        int pageValue = page.orElse(1) - 1;
        int sizeValue = size.orElse(defaultSizePerPage);
        Direction directionValue = direction.orElse(Direction.valueOf(defaultSortDirection));

        return postRepository.findAll(spec,
                        PageRequest.of(
                                pageValue,
                                sizeValue,
                                Sort.by(directionValue, sortBy)))
                .map(postMapper::fromPost);
    }

    /**
     * Returns only published {@code Post} by postId
     *
     * @param  postId {@code Post} entity id
     * @return {@code Optional&lt;PostDto&gt;}
     */
    @Override
    public Optional<PostDto> findById(long postId) {
        Specification<Post> spec = Specification
                .where(PostSpecification.id(postId))
                .and(PostSpecification.condition(EPostCondition.PUBLISHED));

        return postRepository.findOne(spec).map(postMapper::fromPost);
    }

    /**
     * Saves a new or updates existent {@code Post} when {@code User} is active
     *
     * @param  username User "nickname"
     * @param  postDataRequest {@code PostDataRequest}
     * @return {@code PostDto}
     *
     * @throws UserNotFoundException if {@code User} not exist
     * @throws TopicNotFoundException if {@code Topic} not exist
     * @throws ForbiddenException
     *         when {@code User} state(condition) is not {@code EUserCondition.ACTIVE}
     */
    @Transactional
    @Override
    public PostDto save(String username, PostDataRequest postDataRequest) {
        User owner = getUserByUsername(username);
        isActiveUser(owner);

        Topic topic = topicRepository.findByName(postDataRequest.getTopic())
                .orElseThrow(() ->
                        new TopicNotFoundException(
                                String.format(TOPIC_NOT_FOUND_TEMPLATE, postDataRequest.getTopic())));

        Post post;
        Long postId = postDataRequest.getPostId();

        if (postId == null) {
            post = new Post();
            post.setOwner(owner);
        } else {
            post = getPostById(postId);
            isTheOwnerOfPost(owner, post);
        }

        post.setTitle(postDataRequest.getTitle());
        post.setContent(postDataRequest.getContent());
        post.setDescription(postDataRequest.getDescription());
        post.setMainPictureId(postDataRequest.getMainPictureId());
        post.setTopic(topic);

        if (postDataRequest.getTags() != null) {
            post.setTags(getTags(postDataRequest.getTags()));
        }

        Set<Long> picturesIds = postDataRequest.getPicturesIds();

        if (picturesIds != null) {
            Set<Picture> pictures = picturesIds.stream()
                    .map(pictureService::getPictureById)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());

            for (Picture picture : pictures) {
                if (picture.getPost() == null) {
                    picture.setPost(post);
                }
            }

            post.getPictures().retainAll(pictures);
            post.getPictures().addAll(pictures);
        }

        post = postRepository.save(post);

        return postMapper.fromPost(post);
    }

    /**
     * Returns {@code Set&lt;Tag&gt;} from database by {@code Set&lt;String&gt;}
     * if entity not exist in db, creates a new object of {@code Tag}
     *
     * @param  tags {@code Set&lt;String&gt;} of tags
     * @return {@code Set&lt;Tag&gt;}
     */
    private Set<Tag> getTags(Set<String> tags) {

        return tags.stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElse(Tag.builder().name(tagName).build()))
                .collect(Collectors.toSet());
    }

    /**
     * Returns a random {@code Optional&lt;PostDto&gt;} by {@code EPostCondition} if they exist,
     * otherwise - {@code Optional.empty()}
     *
     * @param  postCondition {@code EPostCondition}
     * @return {@code Optional&lt;PostDto&gt;}
     */
    @Override
    public Optional<PostDto> getRandomPost(EPostCondition postCondition) {
        Specification<Post> spec = Specification.where(PostSpecification.condition(postCondition));
        List<Post> posts = postRepository.findAll(spec);

        if (posts.size() > 0) {
            int randomId = ThreadLocalRandom.current().nextInt(posts.size());
            Post randomPost = posts.get(randomId);
            return Optional.of(postMapper.fromPost(randomPost));
        }

        return Optional.empty();
    }

    /**
     * Changes {@code Post} condition to {@code EPostCondition.DELETED}
     *
     * @param  postId {@code Post} entity id
     * @return {@code MessageResponse} when successful
     *
     * @throws PostNotFoundException if {@code Post} not exist
     *
     * @author Zalyaletdinova Ilmira
     */
    @Transactional
    @Override
    public MessageResponse deleteById(Long postId) {
        Post post = getPostById(postId);

        post.setCondition(EPostCondition.DELETED);
        postRepository.save(post);

        return new MessageResponse(
                String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "deleted"));
    }

    /**
     * Changes {@code Post} condition to {@code EPostCondition.DELETED}
     * only when {@code User} is active and is owner of {@code Post}
     *
     * @param  username User "nickname"
     * @param  postId {@code Post} entity id
     * @return {@code MessageResponse} when successful
     *
     * @throws UserNotFoundException if {@code User} not exist
     * @throws ForbiddenException
     *         when {@code User} state(condition) is not {@code EUserCondition.ACTIVE},
     *         and when {@code User} is not owner of provided {@code Post}
     *         and when {@code Post} is not deleted yet
     * @throws PostNotFoundException if {@code Post} not exist
     *
     * @author Zalyaletdinova Ilmira
     */
    @Override
    public MessageResponse delete(String username, long postId) {
        User user = getUserByUsername(username);
        isActiveUser(user);
        Post post = getPostById(postId);
        isTheOwnerOfPost(user, post);

        if (post.getCondition().equals(EPostCondition.DELETED)) {
            throw new ForbiddenException(String.format(POST_DELETED_ALREADY_TEMPLATE, postId));
        }

        return deleteById(postId);
    }

    /**
     * Changes {@code Post} condition to {@code EPostCondition.DELETED}
     * only when {@code User} is active and is {@code EUserCondition.MODERATOR}
     * or {@code EUserCondition.ADMIN}
     *
     * @param  username User "nickname"
     * @param  postId {@code Post} entity id
     * @return {@code MessageResponse} when successful
     *
     * @throws UserNotFoundException if {@code User} not exist
     * @throws ForbiddenException
     *         when {@code User} state(condition) is not {@code EUserCondition.ACTIVE},
     *         and when {@code User} is not MODERATOR or ADMIN
     * @throws PostNotFoundException if {@code Post} not exist
     */
    @Transactional
    @Override
    public MessageResponse deleteAny(String username, long postId) {
        User user = getUserByUsername(username);
        isActiveUser(user);

        Set<ERole> roles = Set.of(ERole.ROLE_MODERATOR, ERole.ROLE_ADMIN);

        if (user.getRoles().stream()
                .map(Role::getName)
                .noneMatch(roles::contains)) {
            throw new ForbiddenException(String.format(USER_MUST_BE_MOD_OR_ADM, username));
        }

        Post post = getPostById(postId);
        post.setCondition(EPostCondition.BANNED);
        postRepository.save(post);

        return new MessageResponse(String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "delete"));
    }

    /**
     * Changes {@code Post} condition to {@code EPostCondition.HIDDEN}
     *
     * @param  postId {@code Post} entity id
     * @return {@code MessageResponse} when successful
     *
     * @throws PostNotFoundException if {@code Post} not exist
     */
    @Transactional
    @Override
    public MessageResponse hideById(Long postId) {
        Post post = getPostById(postId);

        post.setCondition(EPostCondition.HIDDEN);
        postRepository.save(post);

        return new MessageResponse(
                String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "hidden"));
    }

    /**
     * Changes {@code Post} condition to {@code EPostCondition.HIDDEN}
     * only when {@code User} is active and is owner of {@code Post}
     *
     * @param  username User "nickname"
     * @param  postId {@code Post} entity id
     * @return {@code MessageResponse} when successful
     *
     * @throws UserNotFoundException if {@code User} not exist
     * @throws ForbiddenException
     *         when {@code User} state(condition) is not {@code EUserCondition.ACTIVE},
     *         and when {@code User} is not owner of provided {@code Post}
     *         and when {@code Post} is not published yet
     * @throws PostNotFoundException if {@code Post} not exist
     */
    @Override
    public MessageResponse hide(String username, long postId) {
        User user = getUserByUsername(username);
        isActiveUser(user);
        Post post = getPostById(postId);
        isTheOwnerOfPost(user, post);

        if (!post.getCondition().equals(EPostCondition.PUBLISHED)) {
            throw new ForbiddenException("You cannot hide an unpublished post");
        }

        return hideById(postId);
    }

    /**
     * Changes {@code Post} condition to {@code EPostCondition.PUBLISHED}
     * only when {@code User} is active and is owner of {@code Post}
     *
     * @param  username User "nickname"
     * @param  postId {@code Post} entity id
     * @return {@code MessageResponse} when successful
     *
     * @throws UserNotFoundException if {@code User} not exist
     * @throws ForbiddenException
     *         when {@code User} state(condition) is not {@code EUserCondition.ACTIVE},
     *         and when {@code User} is not owner of provided {@code Post}
     * @throws PostNotFoundException if {@code Post} not exist
     * @throws BadRequestException when {@code Post} state(condition) is not
     *         {@code EPostCondition.HIDDEN} or {@code EPostCondition.DRAFT}
     */
    @Override
    @Transactional
    public MessageResponse publish(String username, long postId) {
        User user = getUserByUsername(username);
        isActiveUser(user);
        Post post = getPostById(postId);
        isTheOwnerOfPost(user, post);

        if (!(post.getCondition().equals(EPostCondition.HIDDEN) ||
                post.getCondition().equals(EPostCondition.DRAFT))) {
            throw new BadRequestException(
                    String.format("Post with id [%d] must be DRAFT or HIDDEN", postId));
        }

        post.setCondition(EPostCondition.PUBLISHED);
        postRepository.save(post);

        return new MessageResponse(
                String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "published"));
    }

    /**
     * Changes {@code Post} condition to {@code EPostCondition.BANNNED}
     * only when {@code User} is active and is {@code EUserCondition.MODERATOR}
     * or {@code EUserCondition.ADMIN}
     *
     * @param  username User "nickname"
     * @param  postId {@code Post} entity id
     * @return {@code MessageResponse} when successful
     *
     * @throws UserNotFoundException if {@code User} not exist
     * @throws ForbiddenException
     *         when {@code User} state(condition) is not {@code EUserCondition.ACTIVE},
     *         and when {@code User} is not MODERATOR or ADMIN
     *         and when {@code Post} is not yet PUBLISHED
     * @throws PostNotFoundException if {@code Post} not exist
     */
    @Override
    @Transactional
    public MessageResponse ban(String username, long postId) {
        User user = getUserByUsername(username);
        isActiveUser(user);
        Set<Role> roles = user.getRoles();
        
        for (Role role : roles) {
            if (!(role.getName().equals(ROLE_ADMIN) || role.getName().equals(ROLE_MODERATOR))) {
                throw new ForbiddenException(String.format(USER_MUST_BE_MOD_OR_ADM, username));
            }
        }
        
        Post post = getPostById(postId);
        
        if(!post.getCondition().equals(EPostCondition.PUBLISHED)) {
            throw new ForbiddenException(String.format("Post with id [%d] is not yet PUBLISHED", postId));
        }
        
        post.setCondition(EPostCondition.BANNED);
        postRepository.save(post);

        return new MessageResponse(String.format(POST_WITH_ID_ACTION_SUCCESSFULLY_TEMPLATE, postId, "banned"));
    }

    /**
     * Checks that the {@code User} is owner of {@code Post}.
     *
     * @param     user {@code User} entity
     * @param     post {@code Post} entity
     * @exception ForbiddenException when {@code User} is not owner of this {@code Post}
     */
    private void isTheOwnerOfPost(User user, Post post) {
        if (!user.equals(post.getOwner())) {
            throw new ForbiddenException(NOT_OWNER_TEMPLATE);
        }
    }

    /**
     * Returns the {@code Post} by {@code long postId}
     *
     * @param     postId {@code Post} id
     * @return    the {@code Post}
     * @exception PostNotFoundException if the {@code Post}
     *            with provided {@code postId} does not exist.
     */
    private Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(String.format(POST_NOT_FOUND_TEMPLATE, postId)));
    }


    /**
     * Returns the {@code User} by {@code String username}
     *
     * @param     username User "nickname"
     * @return    the {@code User}
     * @exception UserNotFoundException if the {@code User}
     *            with provided username does not exist.
     */
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format(USER_NOT_FOUND_TEMPLATE, username)));
    }

    /**
     * Checks that the {@code User} has a status - {@code EUserCondition.ACTIVE}.
     *
     * @param     user {@code User} entity
     * @exception ForbiddenException if the {@code User}
     *            "condition" is not {@code EUserCondition.ACTIVE}
     */
    private void isActiveUser(User user) {
        if (!user.getCondition().equals(EUserCondition.ACTIVE)) {
            throw new ForbiddenException(String.format(USER_NOT_ACTIVE_TEMPLATE, user.getUsername()));
        }
    }

}



