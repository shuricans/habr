package no.war.habr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.war.habr.auth.AuthCreator;
import no.war.habr.payload.request.LoginRequest;
import no.war.habr.payload.request.SignupRequest;
import no.war.habr.payload.request.TokenRefreshRequest;
import no.war.habr.payload.response.JwtResponse;
import no.war.habr.persist.model.ERole;
import no.war.habr.persist.model.Role;
import no.war.habr.persist.model.User;
import no.war.habr.persist.repository.RefreshTokenRepository;
import no.war.habr.persist.repository.RoleRepository;
import no.war.habr.persist.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static no.war.habr.util.signin.AuthenticationUtils.clearCache;
import static no.war.habr.util.signin.AuthenticationUtils.signin;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init(@Autowired UserRepository userRepository,
                     @Autowired RoleRepository roleRepository,
                     @Autowired PasswordEncoder passwordEncoder) {
        // create and persist ROLE_USER
        Role roleUser = Role.builder()
                .name(ERole.ROLE_USER)
                .build();
        roleUser = roleRepository.save(roleUser);

        // create and persist user
        User user = User.builder()
                .username(AuthCreator.USERNAME)
                .firstName(AuthCreator.FIRSTNAME)
                .password(passwordEncoder.encode(AuthCreator.PASSWORD))
                .roles(Set.of(roleUser))
                .build();
        userRepository.save(user);
    }

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository,
                         @Autowired RoleRepository roleRepository,
                         @Autowired RefreshTokenRepository refreshTokenRepository) {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("signIn Returns JwtResponse When Successful")
    void signIn_ReturnsJwtResponse_WhenSuccessful() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username(AuthCreator.USERNAME)
                .password(AuthCreator.PASSWORD)
                .build();
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        MockHttpServletRequestBuilder signInRequest = MockMvcRequestBuilders
                .post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mvc.perform(signInRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type", is(AuthCreator.TYPE)))
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.username", is(AuthCreator.USERNAME)))
                .andExpect(jsonPath("$.authorities.[0]", is(ERole.ROLE_USER.name())));
    }

    @Test
    @DisplayName("signIn Returns 400 BadRequest When Bad Credentials")
    void signIn_Returns400BadRequest_WhenBadCredentials() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("notExistentUsername")
                .password(AuthCreator.PASSWORD)
                .build();
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        MockHttpServletRequestBuilder signInRequest = MockMvcRequestBuilders
                .post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mvc.perform(signInRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Credentials")))
                .andExpect(jsonPath("$.details", is("Bad credentials")))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("signUp Save User When Successful")
    void signUp_SaveUser_WhenSuccessful() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .username("newUsername")
                .password(AuthCreator.PASSWORD)
                .firstName(AuthCreator.FIRSTNAME)
                .build();
        String requestBody = objectMapper.writeValueAsString(signupRequest);

        MockHttpServletRequestBuilder signUpRequest = MockMvcRequestBuilders
                .post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mvc.perform(signUpRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));
    }

    @Test
    @DisplayName("signUp Returns 400 BadRequest When User Already Exists")
    void signUp_Returns400BadRequest_WhenUserAlreadyExists() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .username(AuthCreator.USERNAME)
                .password(AuthCreator.PASSWORD)
                .firstName(AuthCreator.FIRSTNAME)
                .build();
        String requestBody = objectMapper.writeValueAsString(signupRequest);

        String expectedDetails = "User with username [" + AuthCreator.USERNAME + "] already exists";

        MockHttpServletRequestBuilder signUpRequest = MockMvcRequestBuilders
                .post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mvc.perform(signUpRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("User Already Exists")))
                .andExpect(jsonPath("$.details", is(expectedDetails)))
                .andExpect(jsonPath("$.developerMessage").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("refreshToken Returns Token Refresh When Successful")
    void refreshToken_ReturnsTokenRefresh_WhenSuccessful() throws Exception {

        clearCache(); // clear test cache, (force authentication)

        JwtResponse jwtResponse = signin(AuthCreator.USERNAME, AuthCreator.PASSWORD, objectMapper, mvc);
        assert jwtResponse != null;
        String refreshToken = jwtResponse.getRefreshToken();

        TokenRefreshRequest refreshRequest = TokenRefreshRequest.builder()
                .refreshToken(refreshToken)
                .build();

        String requestBody = objectMapper.writeValueAsString(refreshRequest);

        MockHttpServletRequestBuilder refreshTokenRequest = MockMvcRequestBuilders
                .post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mvc.perform(refreshTokenRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken", is(refreshToken)))
                .andExpect(jsonPath("$.tokenType", is(AuthCreator.TYPE)));
    }

    @Test
    @DisplayName("logout Removes Refresh Tokens When Successful")
    void logout_RemovesRefreshTokens_WhenSuccessful() throws Exception {
        String token = getAccessToken(AuthCreator.USERNAME, AuthCreator.PASSWORD);

        MockHttpServletRequestBuilder logoutRequest = MockMvcRequestBuilders
                .delete("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(logoutRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Log out successful")));
    }

    @Test
    @DisplayName("logout Returns 401 Unauthorized When Token Not Provided")
    void logout_Returns401Unauthorized_WhenTokenNotProvided() throws Exception {
        MockHttpServletRequestBuilder logoutRequest = MockMvcRequestBuilders
                .delete("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(logoutRequest)
                .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    private String getAccessToken(String username, String password) {
        JwtResponse jwtResponse = signin(
                username,
                password,
                objectMapper,
                mvc);
        assert jwtResponse != null;
        return jwtResponse.getToken();
    }
}