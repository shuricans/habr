package no.war.habr.util.signin;

import lombok.SneakyThrows;
import no.war.habr.payload.request.LoginRequest;
import no.war.habr.payload.response.JwtResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationUtils {

    private static final Map<String, JwtResponse> jwtResponseMap = new HashMap<>();

    @SneakyThrows
    public static JwtResponse signin(String username,
                                     String password,
                                     ObjectMapper objectMapper,
                                     MockMvc mvc) {

        if (jwtResponseMap.containsKey(username)) {
            return jwtResponseMap.get(username);
        }

        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        String requestBody = objectMapper.writeValueAsString(loginRequest);

        MockHttpServletRequestBuilder signInRequest = MockMvcRequestBuilders
                .post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        MvcResult mvcResult = mvc.perform(signInRequest)
                .andExpect(status().isOk()).andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        JwtResponse jwtResponse = objectMapper.readValue(responseBody, JwtResponse.class);
        jwtResponseMap.put(username, jwtResponse);
        return jwtResponse;
    }

    public static void clearCache() {
        jwtResponseMap.clear();
    }
}
