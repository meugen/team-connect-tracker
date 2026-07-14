package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.Project;
import com.ua.teamconnect.tracker.model.entity.Task;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.UserProject;
import com.ua.teamconnect.tracker.model.entity.id.UserProjectTaskId;
import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.repository.*;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest extends AuthorizationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @Autowired
    private UserProjectTaskRepository userProjectTaskRepository;

    @AfterEach
    public void cleanUp() {
        userProjectTaskRepository.deleteAll();
        taskRepository.deleteAll();
        userProjectRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
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
    void create_invalidRole_isForbidden(String role) {
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
    void create_duplicateTaskName_isBadRequest() {
        setupValidToken("user@example.com", "HR");

        var task = new Task();
        task.setName("The task");
        task.setDescription("Implement something useful");
        taskRepository.save(task);

        var body = """
            {
              "name": "The task",
              "description": "Implement something other but still useful"
            }
            """;
        var spec = buildClient(port).post()
            .uri("/tasks")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateBadRequest(spec);
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
    void update_duplicateTaskName_isBadRequest() {
        setupValidToken("user@example.com", "HR");
        var id = createTaskToEdit();

        var task = new Task();
        task.setName("Another task");
        task.setDescription("Implement something else");
        taskRepository.save(task);

        var body = """
            {
              "name": "Another task",
              "description": "Updated description"
            }
            """;
        var spec = buildClient(port).put()
            .uri("/tasks/" + id)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateBadRequest(spec);
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

    private UserProjectTaskIds setupUserProjectTask(LocalDate projectEndDate, LocalDate userProjectEndDate) {
        var user = new User();
        user.setRole("ENGINEER");
        user.setGrade("SENIOR");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setAvatar("https://example.com/avatar.png");
        user.setPhone(Map.of(
            "mobile", "+123456789",
            "work", "+987654321"
        ));
        user.setEmail("user@example.com");
        user.setGender(Gender.MALE);
        user.setStatus("ACTIVE");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user = userRepository.save(user);

        var project = new Project();
        project.setName("Project 1");
        project.setDescription("Project description");
        project.setStatus("ACTIVE");
        project.setIsBillable(true);
        project.setStartDate(LocalDate.now().minusDays(10));
        project.setEndDate(projectEndDate);
        project = projectRepository.save(project);

        var userProject = UserProject.of(user, project);
        userProject.setStartDate(LocalDate.now().minusMonths(5));
        userProject.setEndDate(userProjectEndDate);
        userProjectRepository.save(userProject);

        var task = new Task();
        task.setName("Task 1");
        task.setDescription("Task description");
        task = taskRepository.save(task);

        return new UserProjectTaskIds(user.getId(), project.getId(), task.getId());
    }

    private boolean taskAssigned(UserProjectTaskIds ids) {
        var id = new UserProjectTaskId();
        id.setUserId(ids.userId());
        id.setProjectId(ids.projectId());
        id.setTaskId(ids.taskId());
        return userProjectTaskRepository.existsById(id);
    }

    @ParameterizedTest
    @ValueSource(strings = {"HR", "PM", "ADMIN"})
    void assignTaskToUsersProject_validRequest_isNoContent(String role) {
        setupValidToken("user@example.com", role);
        var ids = setupUserProjectTask(null, null);

        var body = """
            {
              "taskIds": [%d]
            }
            """.formatted(ids.taskId());
        buildClient(port).post()
            .uri("/users/%d/projects/%d/tasks".formatted(ids.userId(), ids.projectId()))
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent();
        assertTrue(taskAssigned(ids));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ENGINEER", "FINANCE"})
    void assignTaskToUsersProject_invalidRequest_isForbidden(String role) {
        setupValidToken("user@example.com", role);
        var ids = setupUserProjectTask(null, null);

        var body = """
            {
              "taskIds": [%d]
            }
            """.formatted(ids.taskId());
        buildClient(port).post()
            .uri("/users/%d/projects/%d/tasks".formatted(ids.userId(), ids.projectId()))
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isForbidden();
        assertFalse(taskAssigned(ids));
    }

    static List<Arguments> validProjectEndDates() {
        return List.of(
            Arguments.of(null, null),
            Arguments.of(LocalDate.now().plusDays(1), null),
            Arguments.of(null, LocalDate.now().plusDays(1)),
            Arguments.of(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1))
        );
    }

    @ParameterizedTest
    @MethodSource("validProjectEndDates")
    void assignTaskToProjectAndUser_validUsersProjectEndDates_isNoContent(
        LocalDate projectEndDate, LocalDate userProjectEndDate
    ) {
        setupValidToken("user@example.com", "HR");
        var ids = setupUserProjectTask(projectEndDate, userProjectEndDate);

        var body = """
            {
              "taskIds": [%d]
            }
            """.formatted(ids.taskId());
        buildClient(port).post()
            .uri("/users/%d/projects/%d/tasks".formatted(ids.userId(), ids.projectId()))
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus().isNoContent();
        assertTrue(taskAssigned(ids));
    }

    static List<Arguments>invalidProjectEndDates() {
        return List.of(
            Arguments.of(LocalDate.now().minusDays(1), null),
            Arguments.of(null, LocalDate.now().minusDays(1)),
            Arguments.of(LocalDate.now().minusDays(1), LocalDate.now().minusDays(1))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidProjectEndDates")
    void assignTaskToProjectAndUser_invalidUsersProjectEndDates_isBadRequest(
        LocalDate projectEndDate, LocalDate userProjectEndDate
    ) {
        setupValidToken("user@example.com", "HR");
        var ids = setupUserProjectTask(projectEndDate, userProjectEndDate);

        var body = """
            {
              "taskIds": [%d]
            }
            """.formatted(ids.taskId());
        var spec = buildClient(port).post()
            .uri("/users/%d/projects/%d/tasks".formatted(ids.userId(), ids.projectId()))
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateBadRequest(spec);
        assertFalse(taskAssigned(ids));
    }

    @Test
    void assignTaskToUsersProjectId_isNotFound() {
        setupValidToken("user@example.com", "HR");
        var ids = setupUserProjectTask(null, null);

        var body = """
            {
              "taskIds": [%d]
            }
            """.formatted(ids.taskId());
        var spec = buildClient(port).post()
            .uri("/users/%d/projects/%d/tasks".formatted(ids.userId() + 1, ids.projectId()))
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateNotFound(spec);
        assertFalse(taskAssigned(ids));
    }

    @Test
    void assignTaskToProjectAndUser_invalidUsersProjectId_isNotFound() {
        setupValidToken("user@example.com", "HR");
        var ids = setupUserProjectTask(null, null);

        var body = """
            {
              "taskIds": [%d]
            }
            """.formatted(ids.taskId());
        var spec = buildClient(port).post()
            .uri("/users/%d/projects/%d/tasks".formatted(ids.userId(), ids.projectId() + 1))
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateNotFound(spec);
        assertFalse(taskAssigned(ids));
    }

    @Test
    void assignTaskToProjectAndUser_invalidTaskId_isNotFoundUsers() {
        setupValidToken("user@example.com", "HR");
        var ids = setupUserProjectTask(null, null);

        var body = """
            {
              "taskIds": [%d]
            }
            """.formatted(ids.taskId() + 1);
        var spec = buildClient(port).post()
            .uri("/users/%d/projects/%d/tasks".formatted(ids.userId(), ids.projectId()))
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange();
        validateNotFound(spec);
        assertFalse(taskAssigned(ids));
    }

    private record UserProjectTaskIds(Integer userId, Integer projectId, Integer taskId) {}
}
