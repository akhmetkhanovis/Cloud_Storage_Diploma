package ru.netology.cloudstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.classic.methods.HttpPost;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClients;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudstorage.model.AuthenticationRequest;
import ru.netology.cloudstorage.model.AuthenticationResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudStorageIntegrationTests {

    @Autowired
    TestRestTemplate restTemplate;

    private static final String HOST = "http://localhost:";
    private static final int PORT = 8081;

    @Container
    private static final GenericContainer<?> cloudStorage = new GenericContainer<>("csapp:latest").withExposedPorts(PORT);

    @TempDir
    Path tempDir;

    @BeforeAll
    public static void setUp() {
        cloudStorage.start();
    }

    @AfterAll
    public static void setDown() {
        cloudStorage.stop();
    }

    private static final String ENDPOINT_LOGIN = "/login";
    private static final String ENDPOINT_LOGOUT = "/logout";
    private static final String ENDPOINT_FILE = "/file";

    public static final String USER = "user";
    public static final String PASSWORD = "user";

    public static final String FILE_TXT = "file.txt";
    public static final String TEST_STRING = "test string";

    private static final String TOKEN_PREFIX = "Bearer ";
    private static String token;

    @Test
    @Order(1)
    void successfulAuthentication() throws IOException {
        AuthenticationRequest authRequest = new AuthenticationRequest(USER, PASSWORD);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                HOST + cloudStorage.getMappedPort(PORT) + ENDPOINT_LOGIN, authRequest, String.class);
        StringReader reader = new StringReader(Objects.requireNonNull(responseEntity.getBody()));
        AuthenticationResponse authResponse = new ObjectMapper().readValue(reader, AuthenticationResponse.class);
        token = authResponse.getToken();
        assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> Assertions.assertNotNull(token)
        );
    }

    @Test
    @Order(2)
    void successfulFileUpload() throws Exception {
        File file = createTempFile();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(HOST + cloudStorage.getMappedPort(PORT) + ENDPOINT_FILE);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addTextBody("filename", file.getName(), ContentType.TEXT_PLAIN)
                .addBinaryBody(
                        "file",
                        new FileInputStream(file),
                        ContentType.APPLICATION_OCTET_STREAM,
                        file.getName()
                );
        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        uploadFile.setHeader("auth-token", TOKEN_PREFIX + token);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        Assertions.assertEquals(HttpStatus.OK, HttpStatus.valueOf(response.getCode()));
    }

    @Test
    @Order(3)
    void logoutSuccess() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                HOST + cloudStorage.getMappedPort(PORT) + ENDPOINT_LOGOUT,
                "", String.class);
        Assertions.assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
    }

    File createTempFile() throws IOException {
        File tempFile = new File(tempDir.toFile(), FILE_TXT);
        Files.write(tempFile.toPath(), TEST_STRING.getBytes());
        return tempFile;
    }
}
