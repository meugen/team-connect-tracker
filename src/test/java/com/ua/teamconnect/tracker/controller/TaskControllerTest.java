package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.List;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest extends AuthorizationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TaskRepository taskRepository;

    @AfterEach
    public void cleanUp() {
        taskRepository.deleteAll();
    }

    @Test
    void create_validRequest_isOk() {
        setupValidToken();

        var body = """
            {
              "name": "The task",
              "description": "Implement something useful"
            }
            """;
        buildClient(port).post()
            .uri("/tasks")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isNumber()
            .jsonPath("$.name").isEqualTo("The task")
            .jsonPath("$.description").isEqualTo("Implement something useful");
    }

    @Test
    void create_invalidToken_iUnauthorized() {
        setupValidToken();

        var body = """
            {
              "name": "The task",
              "description": "Implement something useful"
            }
            """;
        var spec = buildClient(port).post()
            .uri("/tasks")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateUnauthorized(spec);
    }

    static List<Arguments> invalidRequests() {
        return List.of(
            Arguments.of("""
                {
                  "name": "",
                  "description": "Implement something useful"
                }
                """
            ),
            Arguments.of("""
                {
                  "name": "The task",
                  "description": ""
                }
                """
            ),
            Arguments.of("""
                {
                  "name": "",
                  "description": ""
                }
                """
            ),
            Arguments.of("""
                {
                  "name": "The task"
                }
                """
            ),
            Arguments.of("""
                {
                  "description": "Implement something useful"
                }
                """
            ),
            Arguments.of("{}"),
            Arguments.of("hghgbhgnh")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void create_invalidRequest_isBadRequest(String body) {
        setupValidToken();

        var spec = buildClient(port).post()
            .uri("/tasks")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateBadRequest(spec);
    }
}
