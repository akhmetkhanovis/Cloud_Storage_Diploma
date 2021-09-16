package ru.netology.cloudstorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.model.AuthenticationRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    public static final String LIST_LIMIT_3 = "/list?limit=3";
    public static final String USER = "user";
    public static final String PASSWORD = "user";
    public static final String LOGIN = "/login";
    public static final String WRONG_USER = "user23156";
    public static final String WRONG_PASSWORD = "password23156";

    @Autowired
    MockMvc mockMvc;

    @Test
    void successfulAuthentication() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(USER, PASSWORD);
        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(authenticationRequest);

        mockMvc
                .perform(post(LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void incorrectUsername() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(WRONG_USER, PASSWORD);
        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(authenticationRequest);

        mockMvc
                .perform(post(LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void incorrectPassword() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(USER, WRONG_PASSWORD);
        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(authenticationRequest);

        mockMvc
                .perform(post(LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USER, password = PASSWORD)
    void authorizedFilesListRequest() throws Exception {
        mockMvc.perform(get(LIST_LIMIT_3)).andExpect(status().isOk());
    }

    @Test
    void unauthorizedFilesListRequest() throws Exception {
        mockMvc.perform(get(LIST_LIMIT_3)).andExpect(status().isUnauthorized());
    }
}
