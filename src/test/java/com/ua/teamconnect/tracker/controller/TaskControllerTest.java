package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.Task;
import com.ua.teamconnect.tracker.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @ValueSource(strings = {"PM", "HR", "ADMIN"})
    void create_validRequest_isOk(String role) {
        setupValidToken("user@example.com", role);

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

    @ParameterizedTest
    @ValueSource(strings = {"ENGINEER", "FINANCE"})
    void create_invalidRole_isOk(String role) {
        setupValidToken("user@example.com", role);

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
            .expectStatus().isForbidden();
    }

    @Test
    void create_invalidToken_isUnauthorized() {
        setupValidToken("user@example.com", "HR");

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
        setupValidToken("user@example.com", "HR");

        var spec = buildClient(port).post()
            .uri("/tasks")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateBadRequest(spec);
    }

    private Integer createTaskToEdit() {
        var task = new Task();
        task.setName("The task");
        task.setDescription("Implement something useful");
        return taskRepository.save(task).getId();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PM", "HR", "ADMIN"})
    void update_validRequest_isOk(String role) {
        setupValidToken("user@example.com", role);
        var id = createTaskToEdit();

        var body = """
            {
              "name": "Updated task",
              "description": "Updated description"
            }
            """;
        buildClient(port).put()
            .uri("/tasks/" + id)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(id)
            .jsonPath("$.name").isEqualTo("Updated task")
            .jsonPath("$.description").isEqualTo("Updated description");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ENGINEER", "FINANCE"})
    void update_invalidRole_isForbidden(String role) {
        setupValidToken("user@example.com", role);
        var id = createTaskToEdit();

        var body = """
            {
              "name": "Updated task",
              "description": "Updated description"
            }
            """;
        buildClient(port).put()
            .uri("/tasks/" + id)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void update_invalidId_isNotFound() {
        setupValidToken("user@example.com", "HR");
        var id = createTaskToEdit() + 1;

        var body = """
            {
              "name": "Updated task",
              "description": "Updated description"
            }
            """;
        var spec = buildClient(port).put()
            .uri("/tasks/" + id)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateNotFound(spec);
    }

    @Test
    void update_invalidToken_isUnauthorized() {
        setupValidToken("user@example.com", "HR");
        var id = createTaskToEdit();

        var body = """
            {
              "name": "Updated task",
              "description": "Updated description"
            }
            """;
        var spec = buildClient(port).put()
            .uri("/tasks/" + id)
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateUnauthorized(spec);
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void update_invalidRequest_isBadRequest(String body) {
        setupValidToken("user@example.com", "HR");
        var id = createTaskToEdit();

        var spec = buildClient(port).put()
            .uri("/tasks/" + id)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateBadRequest(spec);
    }
}
