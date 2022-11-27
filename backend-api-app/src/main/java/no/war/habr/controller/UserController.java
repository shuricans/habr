package no.war.habr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import no.war.habr.exception.UserNotFoundException;
import no.war.habr.payload.request.ConditionRequest;
import no.war.habr.payload.request.PromoteRequest;
import no.war.habr.payload.request.UpdateUserInfoRequest;
import no.war.habr.payload.response.MessageResponse;
import no.war.habr.persist.model.EUserCondition;
import no.war.habr.persist.model.User;
import no.war.habr.service.UserService;
import no.war.habr.service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@SecurityRequirement(name="bearerAuth")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Returns all users with pagination", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<UserDto>> listAll(@RequestParam("username") Optional<String> username,
                                                 @RequestParam("firstName") Optional<String> firstName,
                                                 @RequestParam("lastName") Optional<String> lastName,
                                                 @RequestParam("condition") Optional<String> condition,
                                                 @RequestParam("page") Optional<Integer> page,
                                                 @RequestParam("size") Optional<Integer> size,
                                                 @RequestParam("sortField") Optional<String> sortField,
                                                 @RequestParam("sortDir") Optional<Direction> sortDir) {

        return ResponseEntity.ok(userService.findAll(
                username,
                firstName,
                lastName,
                condition,
                page,
                size,
                sortField,
                sortDir)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Returns user by id", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "404", description = "When user not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<UserDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + id + " not found.")));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Returns user by username", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "404", description = "When user not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<UserDto> findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User [" + username + "] not found.")));
    }

    @PatchMapping("/promote")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Promote user to others roles", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When request body invalid"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "404", description = "When user not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> promote(@Valid @RequestBody PromoteRequest request) {
        return ResponseEntity.ok(userService.promote(request.getUserId(), request.getRoles()));
    }

    @PatchMapping("/condition")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Changes user condition, to set \"DELETED\" use the delete endpoint", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When request body invalid"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "404", description = "When user not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> condition(@Valid @RequestBody ConditionRequest request) {
        return ResponseEntity.ok(userService.condition(request.getUserId(), request.getCondition()));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Set user condition to DELETED", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "404", description = "When user not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> deleteById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.deleteById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_USER', 'SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Get yourself data", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "404", description = "When user not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<UserDto> getYourselfData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User [" + username + "] not found.")));
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_USER', 'SCOPE_ROLE_MODERATOR', 'SCOPE_ROLE_ADMIN')")
    @Operation(summary = "Updates yourself birthday, firstName, lastName, aboutMe", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When request body invalid"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "404", description = "When user not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<UserDto> update(@Valid @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.update(username, updateUserInfoRequest));
    }

    @GetMapping("/find")
    @Operation(summary = "Returns user by username", tags = "Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "404", description = "When user not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<User>> findByFilter(@PathVariable("username") String username,
                                                   @PathVariable("birthday") LocalDate birthday,
                                                   @PathVariable("condition") EUserCondition condition) {
        return ResponseEntity.ok(userService.findByFilter(username, birthday, condition));
    }
}
