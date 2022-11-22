package no.war.habr.service;

import lombok.RequiredArgsConstructor;
import no.war.habr.exception.BadRequestException;
import no.war.habr.exception.PostNotFoundException;
import no.war.habr.exception.TopicNotFoundException;
import no.war.habr.exception.UserNotFoundException;
import no.war.habr.payload.request.PostDataRequest;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    public Page<PostDto> findAll(Optional<String> topic,
                                 Optional<String> tag,
                                 Optional<String> condition,
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
        try {
            if (condition.isPresent()) {
                spec = combineSpec(spec,
                        PostSpecification
                                .condition(EPostCondition.valueOf(condition.get().toUpperCase())));
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
        return postRepository.findById(postId).map(postMapper::fromPost);
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
                        .orElse(tagRepository.save(Tag.builder().name(tagName).build())))
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
}
