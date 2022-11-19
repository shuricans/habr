package no.war.habr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import no.war.habr.service.PictureService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PictureController
 *
 * @author Karachev Sasha
 * @see no.war.habr.persist.model.Picture
 */
@RestController
@RequestMapping("/picture")
@AllArgsConstructor
public class PictureController {

    private final PictureService pictureService;

    @GetMapping("/{pictureId}")
    @Operation(summary = "Returns an array of image bytes", tags = "Pictures")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "When not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<byte[]> downloadPicture(@PathVariable("pictureId") long pictureId) {
        return pictureService.getPictureDataById(pictureId)
                .map(pic -> ResponseEntity
                        .ok()
                        .header(HttpHeaders.CONTENT_TYPE, pic.getContentType())
                        .body(pic.getData()))
                .orElse(ResponseEntity.notFound().build());
    }
}
