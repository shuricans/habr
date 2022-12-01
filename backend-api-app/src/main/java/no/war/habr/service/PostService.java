package no.war.habr.service;

import no.war.habr.payload.request.PostDataRequest;
import no.war.habr.payload.response.MessageResponse;
import no.war.habr.persist.model.EPostCondition;
import no.war.habr.service.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Optional;

/**
 * Interface describing the contract of interaction with the PostDto entity.
 * @see PostDto
 *
 * @author Karachev Sasha
 */
public interface PostService {

    Page<PostDto> findAll(Optional<String> username,
                          Optional<String> topic,
                          Optional<String> tag,
                          Optional<String> condition,
                          Optional<String> excludeCondition,
                          Optional<Integer> page,
                          Optional<Integer> size,
                          Optional<String> sortField,
                          Optional<Sort.Direction> direction);

    Optional<PostDto> findById(long postId);

    PostDto save(String username, PostDataRequest postDataRequest);

    Optional<PostDto> getRandomPost(EPostCondition postCondition);

    MessageResponse deleteById(Long postId);
    MessageResponse delete(String username, long postId);
}
