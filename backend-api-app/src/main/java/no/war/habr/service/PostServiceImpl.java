package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.exception.*;
import no.war.habr.payload.request.PostDataRequest;
import no.war.habr.payload.response.MessageResponse;
import no.war.habr.persist.model.*;
import no.war.habr.persist.repository.PostRepository;
import no.war.habr.persist.repository.TagRepository;
import no.war.habr.persist.repository.TopicRepository;
import no.war.habr.persist.repository.UserRepository;
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
import java.util.*;
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
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Value("${app.defaultSizePerPage.posts}")
    private int defaultSizePerPage;

    @Value("${app.defaultSortField.posts}")
    private String defaultSortField;

    @Value("${app.defaultSortDirection.posts}")
    private String defaultSortDirection;

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

    @Override
    public Optional<PostDto> findById(long postId) {
        Specification<Post> spec = Specification
                .where(PostSpecification.id(postId))
                .and(PostSpecification.condition(EPostCondition.PUBLISHED));
        return postRepository.findOne(spec).map(postMapper::fromPost);
    }

    @Transactional
    @Override
    public PostDto save(String username, PostDataRequest postDataRequest) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User with username = " + username + " not found."));

        Topic topic = topicRepository.findByName(postDataRequest.getTopic())
                .orElseThrow(() ->
                        new TopicNotFoundException("Topic by name = " + postDataRequest.getTopic() + " does not exist."));

        Post post;
        Long postId = postDataRequest.getPostId();
        if (postId == null) {
            post = new Post();
            post.setOwner(owner);
        } else {
            post = postRepository.findById(postId).orElseThrow(() ->
                    new PostNotFoundException("Post with id = " + postId + " not found."));
            if (!owner.equals(post.getOwner())) {
                throw new BadRequestException("You are not the owner of this post!");
            }
        }
        post.setTitle(postDataRequest.getTitle());
        post.setContent(postDataRequest.getContent());
        post.setDescription(postDataRequest.getDescription());
        post.setTopic(topic);
        if (postDataRequest.getTags() != null) {
            post.setTags(getTags(postDataRequest.getTags()));
        }

        post = postRepository.save(post);

        return postMapper.fromPost(post);
    }

    private Set<Tag> getTags(Set<String> tags) {
        return tags.stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElse(Tag.builder().name(tagName).build()))
                .collect(Collectors.toSet());
    }

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
     * Delete post by id
     *
     * @author Zalyaletdinova Ilmira
     */
    @Transactional
    @Override
    public MessageResponse deleteById(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException(String.format("Post by id [%d] not found.", postId)));

        post.setCondition(EPostCondition.DELETED);
        postRepository.save(post);

        return new MessageResponse(String.format("Post with id [%d] deleted successfully", postId));
    }


    /**
     * The delete method with a check on the condition of the user and the post
     *
     * @author Zalyaletdinova Ilmira
     */
    @Override
    public MessageResponse delete(String username, long postId) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format("User with username [%s] not found.", username)));

        if (!owner.getCondition().equals(EUserCondition.ACTIVE)) {
            throw new PreconditionFailedException(String.format("User [%s] is not ACTIVE", username));
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(String.format("Post by id [%d] not found.", postId)));

        if (!owner.equals(post.getOwner())) {
            throw new PreconditionFailedException("You are not the owner of this post!");
        }

        if (post.getCondition().equals(EPostCondition.DELETED)) {
            throw new PreconditionFailedException(String.format("Post by id [%d] deleted already", postId));
        }

        return deleteById(postId);
    }

    @Transactional
    @Override
    public MessageResponse hideById(Long postId) {
        Post post = getPostById(postId);

        post.setCondition(EPostCondition.HIDDEN);
        postRepository.save(post);

        return new MessageResponse(String.format("Post with id [%d] hidden successfully", postId));
    }

    @Override
    public MessageResponse hide(String username, long postId) {
        User user = getUserByUsername(username);
        isActiveUser(user);
        Post post = getPostById(postId);
        isTheOwnerOfPost(user, post);

        if(!post.getCondition().equals(EPostCondition.PUBLISHED)) {
            throw new ForbiddenException("You cannot hide an unpublished post");
        }

        return hideById(postId);
    }

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

        return new MessageResponse(String.format("Post with id [%d] published successfully", postId));
    }

    @Override
    @Transactional
    public MessageResponse ban(String username, long postId) {
        User user = getUserByUsername(username);
        isActiveUser(user);
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            if (!(role.getName().equals(ROLE_ADMIN) || role.getName().equals(ROLE_MODERATOR))) {
                throw new ForbiddenException(String.format("User with username: [%s] must have rights" +
                        " ADMIN or MODERATOR", username));
            }
        }
        Post post = getPostById(postId);
        if(!post.getCondition().equals(EPostCondition.PUBLISHED)) {
            throw new ForbiddenException(String.format("Post with id: [%d] is not PUBLISHED", postId));
        }
        post.setCondition(EPostCondition.BANNED);
        postRepository.save(post);

        return new MessageResponse(String.format("Post with id [%d] baned successfully", postId));
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



