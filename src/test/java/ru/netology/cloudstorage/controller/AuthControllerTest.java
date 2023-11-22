package ru.netology.cloudstorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.model.AuthenticationRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String LIST_ENDPOINT = "/list?limit=3";
    private static final String USER = "user";
    private static final String PASSWORD = "user";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String WRONG_USER = "user23156";
    private static final String WRONG_PASSWORD = "password23156";

    @Autowired
    MockMvc mockMvc;

    @Test
    void successfulAuthentication() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(USER, PASSWORD);
        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(authenticationRequest);

        mockMvc
                .perform(post(LOGIN_ENDPOINT)
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
                .perform(post(LOGIN_ENDPOINT)
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
                .perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USER, password = PASSWORD)
    void authorizedFilesListRequest() throws Exception {
        mockMvc.perform(get(LIST_ENDPOINT)).andExpect(status().isOk());
    }

    @Test
    void unauthorizedFilesListRequest() throws Exception {
        mockMvc.perform(get(LIST_ENDPOINT)).andExpect(status().isUnauthorized());
    }
}
