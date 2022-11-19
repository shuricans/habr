package no.war.habr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.war.habr.exception.PostNotFoundException;
import no.war.habr.service.PostService;
import no.war.habr.service.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Post controller, api for posts.
 *
 * @author Karachev Sasha
 * @see no.war.habr.persist.model.Post
 * @see no.war.habr.service.PostService
 */
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "Returns all posts with pagination", tags = "Posts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<PostDto>> listAll(@RequestParam("topic") Optional<String> topic,
                                                 @RequestParam("tag") Optional<String> tag,
                                                 @RequestParam("condition") Optional<String> condition,
                                                 @RequestParam("page") Optional<Integer> page,
                                                 @RequestParam("size") Optional<Integer> size,
                                                 @RequestParam("sortField") Optional<String> sortField,
                                                 @RequestParam("sortDir") Optional<Direction> sortDir) {

        return ResponseEntity.ok(
                postService.findAll(
                        topic,
                        tag,
                        condition,
                        page,
                        size,
                        sortField,
                        sortDir)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns post by id", tags = "Posts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "When post not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<PostDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id = " + id + " not found.")));
    }
}
