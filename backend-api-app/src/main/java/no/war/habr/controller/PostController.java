package no.war.habr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.war.habr.exception.PostNotFoundException;
import no.war.habr.payload.request.PostDataRequest;
import no.war.habr.persist.model.EPostCondition;
import no.war.habr.service.PostService;
import no.war.habr.service.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Post controller, api for posts.
 *
 * @author Karachev Sasha
 * @see no.war.habr.persist.model.Post
 * @see no.war.habr.service.PostService
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "Returns all published posts with pagination", tags = "Posts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<PostDto>> listAll(@RequestParam("username") Optional<String> username,
                                                 @RequestParam("topic") Optional<String> topic,
                                                 @RequestParam("tag") Optional<String> tag,
                                                 @RequestParam("page") Optional<Integer> page,
                                                 @RequestParam("size") Optional<Integer> size,
                                                 @RequestParam("sortField") Optional<String> sortField,
                                                 @RequestParam("sortDir") Optional<Direction> sortDir) {

        return ResponseEntity.ok(
                postService.findAll(
                        username,
                        topic,
                        tag,
                        Optional.of(EPostCondition.PUBLISHED.name()),
                        Optional.empty(),
                        page,
                        size,
                        sortField,
                        sortDir)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns published post by id", tags = "Posts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "When post not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<PostDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id = " + id + " not found.")));
    }

    @GetMapping("/random")
    @Operation(summary = "Returns random published post", tags = "Posts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "When post not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<PostDto> random() {
        return ResponseEntity.ok(postService.getRandomPost(EPostCondition.PUBLISHED)
                .orElseThrow(() -> new PostNotFoundException("Post not found.")));
    }

    @PostMapping("/save")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_USER', 'SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Saves a new or updates an existing post", tags = "Posts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful saved/updated"),
            @ApiResponse(responseCode = "400", description = "When request body invalid"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "404", description = "When post or topic not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<PostDto> save(@Valid @RequestBody PostDataRequest postDataRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(postService.save(username, postDataRequest));
    }

    @GetMapping("/own")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_USER', 'SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Returns own posts with pagination, except for DELETED", tags = "Posts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When bad request"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<PostDto>> listOwnPosts(@RequestParam("topic") Optional<String> topic,
                                                      @RequestParam("tag") Optional<String> tag,
                                                      @RequestParam("condition") Optional<String> condition,
                                                      @RequestParam("page") Optional<Integer> page,
                                                      @RequestParam("size") Optional<Integer> size,
                                                      @RequestParam("sortField") Optional<String> sortField,
                                                      @RequestParam("sortDir") Optional<Direction> sortDir) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(
                postService.findAll(
                        Optional.of(username),
                        topic,
                        tag,
                        condition,
                        Optional.of(EPostCondition.DELETED.name()),
                        page,
                        size,
                        sortField,
                        sortDir)
        );
    }
}
