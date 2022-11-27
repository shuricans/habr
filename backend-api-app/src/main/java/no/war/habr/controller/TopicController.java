package no.war.habr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import no.war.habr.service.TopicService;
import no.war.habr.service.dto.TopicDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * TopicController.
 *
 * @author Zalyaletdinova Ilmira
 */

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    @Operation(summary = "Returns all topics", tags = "Topics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "When topic not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<List<TopicDto>> findAll() {
        return ResponseEntity.ok(topicService.findAll());
    }
}
