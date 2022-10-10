package no.war.habr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.war.habr.payload.request.ConditionRequest;
import no.war.habr.payload.request.PromoteRequest;
import no.war.habr.payload.response.JwtResponse;
import no.war.habr.persist.model.ERole;
import no.war.habr.persist.model.EUserCondition;
import no.war.habr.persist.model.Role;
import no.war.habr.persist.model.User;
import no.war.habr.persist.repository.RefreshTokenRepository;
import no.war.habr.persist.repository.RoleRepository;
import no.war.habr.persist.repository.UserRepository;
import no.war.habr.util.user.UserCreator;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void beforeEach() {
        Role roleUser = roleRepository.save(getRole(ERole.ROLE_USER));
        Role roleModerator = roleRepository.save(getRole(ERole.ROLE_MODERATOR));
        Role roleAdmin = roleRepository.save(getRole(ERole.ROLE_ADMIN));

        User user = getUser(UserCreator.USERNAME, roleUser, passwordEncoder);
        User moderator = getUser(UserCreator.MODERATOR, roleModerator, passwordEncoder);
        User admin = getUser(UserCreator.ADMIN, roleAdmin, passwordEncoder);

        userRepository.saveAll(List.of(user, moderator, admin));
    }

    @AfterEach
    void afterEach() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("listAll Returns 401 Unauthorized When Token Not Provided")
    void listAll_Returns401Unauthorized_WhenTokenNotProvided() throws Exception {
        MockHttpServletRequestBuilder listAllUsersRequest = MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(listAllUsersRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("listAll Returns 403 Forbidden When Insufficient Rights")
    void listAll_Returns403Forbidden_WhenInsufficientRights() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.USERNAME, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        MockHttpServletRequestBuilder listAllUsersRequest = MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(listAllUsersRequest)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Access Denied")))
                .andExpect(jsonPath("$.details", is("Access is denied")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("listAll Returns List Of Users Inside Page Object When Successful")
    void listAll_ReturnsListOfUsersInsidePageObject_WhenSuccessful() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.MODERATOR, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        MockHttpServletRequestBuilder listAllUsersRequest = MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(listAllUsersRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(3)))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.last").exists())
                .andExpect(jsonPath("$.sort").exists())
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(3)))
                .andExpect(jsonPath("$.empty", is(false)));
    }

    @Test
    @DisplayName("findById Returns 401 Unauthorized When Token Not Provided")
    void findById_Returns401Unauthorized_WhenTokenNotProvided() throws Exception {
        MockHttpServletRequestBuilder findByIdRequest = MockMvcRequestBuilders
                .get("/users/1")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(findByIdRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("findById Returns 403 Forbidden When Insufficient Rights")
    void findById_Returns403Forbidden_WhenInsufficientRights() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.USERNAME, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        MockHttpServletRequestBuilder findByIdRequest = MockMvcRequestBuilders
                .get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(findByIdRequest)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Access Denied")))
                .andExpect(jsonPath("$.details", is("Access is denied")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("findById Returns User By Id When Successful")
    void findById_ReturnsUserById_WhenSuccessful() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.ADMIN, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        Optional<User> optionalUser = userRepository.findByUsername(UserCreator.USERNAME);
        assert optionalUser.isPresent();
        User expectedUser = optionalUser.get();
        Optional<Role> optionalRole = expectedUser.getRoles().stream().findFirst();
        assert optionalRole.isPresent();
        Role expectedRole = optionalRole.get();

        MockHttpServletRequestBuilder findByIdRequest = MockMvcRequestBuilders
                .get("/users/" + expectedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(findByIdRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(expectedUser.getUsername())))
                .andExpect(jsonPath("$.firstName", is(expectedUser.getFirstName())))
                .andExpect(jsonPath("$.lastName").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.aboutMe").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.birthday").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.condition", is(expectedUser.getCondition().name())))
                .andExpect(jsonPath("$.roles.[0]", is(expectedRole.getName().name())));
    }

    @Test
    @DisplayName("findByUsername Returns 401 Unauthorized When Token Not Provided")
    void findByUsername_Returns401Unauthorized_WhenTokenNotProvided() throws Exception {
        MockHttpServletRequestBuilder findByUsernameRequest = MockMvcRequestBuilders
                .get("/users/username/" + UserCreator.USERNAME)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(findByUsernameRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("findByUsername Returns 403 Forbidden When Insufficient Rights")
    void findByUsername_Returns403Forbidden_WhenInsufficientRights() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.USERNAME, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        MockHttpServletRequestBuilder findByUsernameRequest = MockMvcRequestBuilders
                .get("/users/username/" + UserCreator.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(findByUsernameRequest)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Access Denied")))
                .andExpect(jsonPath("$.details", is("Access is denied")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("findByUsername Returns User By Id When Successful")
    void findByUsername_ReturnsUserById_WhenSuccessful() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.ADMIN, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        Optional<User> optionalUser = userRepository.findByUsername(UserCreator.USERNAME);
        assert optionalUser.isPresent();
        User expectedUser = optionalUser.get();
        Optional<Role> optionalRole = expectedUser.getRoles().stream().findFirst();
        assert optionalRole.isPresent();
        Role expectedRole = optionalRole.get();

        MockHttpServletRequestBuilder findByUsernameRequest = MockMvcRequestBuilders
                .get("/users/username/" + UserCreator.USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(findByUsernameRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(expectedUser.getUsername())))
                .andExpect(jsonPath("$.firstName", is(expectedUser.getFirstName())))
                .andExpect(jsonPath("$.lastName").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.aboutMe").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.birthday").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.condition", is(expectedUser.getCondition().name())))
                .andExpect(jsonPath("$.roles.[0]", is(expectedRole.getName().name())));
    }

    @Test
    @DisplayName("promote Returns 401 Unauthorized When Token Not Provided")
    void promote_Returns401Unauthorized_WhenTokenNotProvided() throws Exception {
        PromoteRequest promoteRequest = PromoteRequest.builder()
                .userId(1L)
                .roles(Set.of("ROLE_USER"))
                .build();

        String jsonPromoteRequest = objectMapper.writeValueAsString(promoteRequest);

        MockHttpServletRequestBuilder promoteRequestBuilder = MockMvcRequestBuilders
                .patch("/users/promote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPromoteRequest);

        mvc.perform(promoteRequestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("promote Returns 403 Forbidden When Insufficient Rights")
    void promote_Returns403Forbidden_WhenInsufficientRights() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.USERNAME, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        PromoteRequest promoteRequest = PromoteRequest.builder()
                .userId(1L)
                .roles(Set.of("ROLE_USER"))
                .build();

        String jsonPromoteRequest = objectMapper.writeValueAsString(promoteRequest);

        MockHttpServletRequestBuilder promoteRequestBuilder = MockMvcRequestBuilders
                .patch("/users/promote")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonPromoteRequest);

        mvc.perform(promoteRequestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Access Denied")))
                .andExpect(jsonPath("$.details", is("Access is denied")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("promote Updates User Roles When Successful")
    void promote_UpdatesUserRoles_WhenSuccessful() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.ADMIN, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        Optional<User> optionalUser = userRepository.findByUsername(UserCreator.USERNAME);
        assert optionalUser.isPresent();
        User user = optionalUser.get();

        PromoteRequest promoteRequest = PromoteRequest.builder()
                .userId(user.getId())
                .roles(Set.of("ROLE_MODERATOR"))
                .build();

        String jsonPromoteRequest = objectMapper.writeValueAsString(promoteRequest);
        String expectedMessage = String.format("User [%s] promoted.", user.getUsername());

        MockHttpServletRequestBuilder promoteRequestBuilder = MockMvcRequestBuilders
                .patch("/users/promote")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonPromoteRequest);

        mvc.perform(promoteRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DisplayName("promote Returns 403 Forbidden When Try Promote Yourself")
    void promote_Returns403Forbidden_WhenTryPromoteYourself() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.ADMIN, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        Optional<User> optionalUser = userRepository.findByUsername(UserCreator.ADMIN);
        assert optionalUser.isPresent();
        User admin = optionalUser.get();

        PromoteRequest promoteRequest = PromoteRequest.builder()
                .userId(admin.getId())
                .roles(Set.of("ROLE_MODERATOR", "ROLE_ADMIN"))
                .build();

        String jsonPromoteRequest = objectMapper.writeValueAsString(promoteRequest);

        MockHttpServletRequestBuilder promoteRequestBuilder = MockMvcRequestBuilders
                .patch("/users/promote")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonPromoteRequest);

        mvc.perform(promoteRequestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Forbidden.")))
                .andExpect(jsonPath("$.details", is("Not allowed to edit yourself.")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("promote Returns 404 Not Found When Promoted User Does Not Exists")
    void promote_Returns404NotFound_WhenPromotedUserDoesNotExists() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.ADMIN, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        long wrongUserId = 250L;
        PromoteRequest promoteRequest = PromoteRequest.builder()
                .userId(wrongUserId)
                .roles(Set.of("ROLE_MODERATOR"))
                .build();

        String jsonPromoteRequest = objectMapper.writeValueAsString(promoteRequest);
        String expectedMessage = String.format("User with id = %d not found.", wrongUserId);

        MockHttpServletRequestBuilder promoteRequestBuilder = MockMvcRequestBuilders
                .patch("/users/promote")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonPromoteRequest);

        mvc.perform(promoteRequestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("User not found.")))
                .andExpect(jsonPath("$.details", is(expectedMessage)))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("condition Returns 401 Unauthorized When Token Not Provided")
    void condition_Returns401Unauthorized_WhenTokenNotProvided() throws Exception {
        ConditionRequest conditionRequest = ConditionRequest.builder()
                .userId(1L)
                .condition(EUserCondition.BANNED)
                .build();

        String jsonConditionRequest = objectMapper.writeValueAsString(conditionRequest);

        MockHttpServletRequestBuilder conditionRequestBuilder = MockMvcRequestBuilders
                .patch("/users/condition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonConditionRequest);

        mvc.perform(conditionRequestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("condition Returns 400 Bad Request When Request Body Invalid")
    void condition_Returns400BadRequest_WhenRequestBodyInvalid() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.ADMIN, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        ConditionRequest conditionRequest = ConditionRequest.builder()
                .userId(1L)
                .build();

        String jsonConditionRequest = objectMapper.writeValueAsString(conditionRequest);

        MockHttpServletRequestBuilder conditionRequestBuilder = MockMvcRequestBuilders
                .patch("/users/condition")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonConditionRequest);

        mvc.perform(conditionRequestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Check the fields of the sent object.")))
                .andExpect(jsonPath("$.details", is("There was a validation error in one of the object's properties, please enter correct values.")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.fieldErrors.condition.[0]", is("must not be null")));
    }

    @Test
    @DisplayName("condition Returns 403 Forbidden When Insufficient Rights")
    void condition_Returns403Forbidden_WhenInsufficientRights() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.USERNAME, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        ConditionRequest conditionRequest = ConditionRequest.builder()
                .userId(1L)
                .condition(EUserCondition.NOT_ACTIVE)
                .build();

        String jsonConditionRequest = objectMapper.writeValueAsString(conditionRequest);

        MockHttpServletRequestBuilder conditionRequestBuilder = MockMvcRequestBuilders
                .patch("/users/condition")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonConditionRequest);

        mvc.perform(conditionRequestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Access Denied")))
                .andExpect(jsonPath("$.details", is("Access is denied")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("condition Returns 403 Forbidden When Try Edit Yourself")
    void condition_Returns403Forbidden_WhenTryEditYourself() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.MODERATOR, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        Optional<User> optionalUser = userRepository.findByUsername(UserCreator.MODERATOR);
        assert optionalUser.isPresent();
        User moderator = optionalUser.get();

        ConditionRequest conditionRequest = ConditionRequest.builder()
                .userId(moderator.getId())
                .condition(EUserCondition.BANNED)
                .build();

        String jsonConditionRequest = objectMapper.writeValueAsString(conditionRequest);

        MockHttpServletRequestBuilder conditionRequestBuilder = MockMvcRequestBuilders
                .patch("/users/condition")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonConditionRequest);

        mvc.perform(conditionRequestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Forbidden.")))
                .andExpect(jsonPath("$.details", is("Not allowed to edit yourself.")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("condition Returns 403 Forbidden When Try Edit User With Higher Role")
    void condition_Returns403Forbidden_WhenTryEditUserWithHigherRole() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.MODERATOR, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        Optional<User> optionalUser = userRepository.findByUsername(UserCreator.ADMIN);
        assert optionalUser.isPresent();
        User admin = optionalUser.get();
        Set<ERole> roles = admin.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        ConditionRequest conditionRequest = ConditionRequest.builder()
                .userId(admin.getId())
                .condition(EUserCondition.BANNED)
                .build();

        String jsonConditionRequest = objectMapper.writeValueAsString(conditionRequest);
        String expectedMessage = String.format("Insufficient rights to edit user with roles %s", roles);

        MockHttpServletRequestBuilder conditionRequestBuilder = MockMvcRequestBuilders
                .patch("/users/condition")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonConditionRequest);

        mvc.perform(conditionRequestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("Forbidden.")))
                .andExpect(jsonPath("$.details", is(expectedMessage)))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(HttpStatus.FORBIDDEN.value())))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("condition Changes Condition When Successful")
    void condition_ChangesCondition_WhenSuccessful() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.MODERATOR, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        Optional<User> optionalUser = userRepository.findByUsername(UserCreator.USERNAME);
        assert optionalUser.isPresent();
        User user = optionalUser.get();
        String prevCondition = user.getCondition().name();

        ConditionRequest conditionRequest = ConditionRequest.builder()
                .userId(user.getId())
                .condition(EUserCondition.BANNED)
                .build();

        String jsonConditionRequest = objectMapper.writeValueAsString(conditionRequest);
        String expectedMessage = String.format(
                "Successfully changed condition from [%s] to [%s] for User [%s].",
                prevCondition,
                EUserCondition.BANNED.name(),
                UserCreator.USERNAME);

        MockHttpServletRequestBuilder conditionRequestBuilder = MockMvcRequestBuilders
                .patch("/users/condition")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(jsonConditionRequest);

        mvc.perform(conditionRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DisplayName("delete Returns 401 Unauthorized When Token Not Provided")
    void delete_Returns401Unauthorized_WhenTokenNotProvided() throws Exception {
        MockHttpServletRequestBuilder deleteRequestBuilder = MockMvcRequestBuilders
                .delete("/users/delete/1")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(deleteRequestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("delete Changes Condition To DELETED When Successful")
    void delete_ChangesConditionToDELETED_WhenSuccessful() throws Exception {
        JwtResponse jwtResponse = getJwtResponseAfterSignin(UserCreator.MODERATOR, UserCreator.PASSWORD);
        assert jwtResponse != null;
        String token = jwtResponse.getToken();

        Optional<User> optionalUser = userRepository.findByUsername(UserCreator.USERNAME);
        assert optionalUser.isPresent();
        User user = optionalUser.get();

        String expectedMessage = String.format("User [%s] condition successfully changed to DELETED", user.getUsername());

        MockHttpServletRequestBuilder deleteRequestBuilder = MockMvcRequestBuilders
                .delete("/users/delete/" + user.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(deleteRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    private JwtResponse getJwtResponseAfterSignin(String username, String password) {
        String loginRequest = getLoginRequestBody(username, password);
        MockHttpServletRequestBuilder signInRequest = MockMvcRequestBuilders
                .post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest);

        try {
            MvcResult mvcResult = mvc.perform(signInRequest)
                    .andExpect(status().isOk()).andReturn();
            String contentAsString = mvcResult.getResponse().getContentAsString();
            return objectMapper.readValue(contentAsString, JwtResponse.class);
        } catch (Exception ignore) {
        }
        return null;
    }

    private String getLoginRequestBody(String username, String password) {
        return String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                username, password);
    }

    private static User getUser(String username, Role role, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .firstName(UserCreator.FIRSTNAME)
                .password(passwordEncoder.encode(UserCreator.PASSWORD))
                .roles(Set.of(role))
                .build();
    }

    private static Role getRole(ERole role) {
        return Role.builder().name(role).build();
    }
}