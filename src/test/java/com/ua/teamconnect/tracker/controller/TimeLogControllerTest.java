package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimeLogControllerTest extends AuthorizationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @Autowired
    private UserProjectTaskRepository userProjectTaskRepository;

    @Autowired
    private TimeLogRepository timeLogRepository;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @AfterEach
    void cleanUp() {
        timeLogRepository.deleteAll();
        userProjectTaskRepository.deleteAll();
        userProjectRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
        taskRepository.deleteAll();
        mediaFileRepository.deleteAll();
    }

    private User newUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user.setStatus("ACTIVE");
        user.setGender(Gender.MALE);
        user.setGrade("Senior");
        user.setRole("ENGINEER");
        user.setPhone(Map.of(
            "mobile", "+123456789",
            "home", "+98765321"
        ));
        user.setBirthDate(LocalDate.of(1990, Month.FEBRUARY, 20));
        return userRepository.save(user);
    }

    private Integer createData() {
        var pm = newUser("pm@example.com");
        newUser("pm-no-projects@example.com");
        var user = newUser("engineer@example.com");

        var project1 = new Project();
        project1.setName("Project 1");
        project1.setDescription("Project 1 description");
        project1.setStatus("ACTIVE");
        project1.setIsBillable(true);
        project1.setStartDate(LocalDate.now().minusYears(1));
        project1 = projectRepository.save(project1);

        var project2 = new Project();
        project2.setName("Project 2");
        project2.setDescription("Project 2 description");
        project2.setStatus("ACTIVE");
        project2.setIsBillable(true);
        project2.setStartDate(LocalDate.now().minusYears(1));
        project2 = projectRepository.save(project2);

        var project3 = new Project();
        project3.setName("Project 3");
        project3.setDescription("Project 3 description");
        project3.setStatus("ACTIVE");
        project3.setIsBillable(true);
        project3.setStartDate(LocalDate.now().minusYears(1));
        project3 = projectRepository.save(project3);

        var userProject = UserProject.of(pm, project1);
        userProject.setStartDate(LocalDate.now().minusWeeks(1));
        userProject.setRole("PM");
        userProjectRepository.save(userProject);

        userProject = UserProject.of(pm, project2);
        userProject.setStartDate(LocalDate.now().minusWeeks(1));
        userProject.setRole("PM");
        userProjectRepository.save(userProject);

        userProject = UserProject.of(user, project2);
        userProject.setStartDate(LocalDate.now().minusWeeks(1));
        userProject.setRole("DEVELOPER");
        userProjectRepository.save(userProject);

        userProject = UserProject.of(user, project3);
        userProject.setStartDate(LocalDate.now().minusWeeks(1));
        userProject.setRole("DEVELOPER");
        userProjectRepository.save(userProject);

        var task = new Task();
        task.setName("Task 1");
        task.setDescription("Task 1 description");
        task = taskRepository.save(task);

        var userProjectTask = userProjectTaskRepository.save(UserProjectTask.of(user, project2, task));

        var mediaFile = new MediaFile();
        mediaFile.setCreatedAt(LocalDateTime.now());
        mediaFile.setUpdatedAt(LocalDateTime.now());
        mediaFile.setSize(100L);
        mediaFile.setUrl("https://example.com/mediafile");
        mediaFile.setContentType("image/png");
        mediaFile.setDropboxPath("/path/to/mediafile");
        mediaFile = mediaFileRepository.save(mediaFile);

        var timeLog = new TimeLog();
        timeLog.setUserProjectTask(userProjectTask);
        timeLog.setDate(LocalDate.now());
        timeLog.setDurationMinutes(100);
        timeLog.setDescription("Worked on Task 1");
        timeLog.setMediaFile(mediaFile);
        timeLogRepository.save(timeLog);

        return user.getId();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PM", "HR", "ADMIN"})
    void findForUser_validRole_isOk(String role) {
        setupValidToken("pm@example.com", role);
        var userId = createData();

        var startDate = LocalDate.now().minusDays(1);
        var endDate = LocalDate.now().plusDays(1);
        buildClient(port).get()
            .uri("/time-logs?userId={userId}&startDate={startDate}&endDate={endDate}", userId, startDate, endDate)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].description").isEqualTo("Worked on Task 1")
            .jsonPath("$[0].durationMinutes").isEqualTo(100)
            .jsonPath("$[0].date").isEqualTo(LocalDate.now().toString())
            .jsonPath("$[0].mediaFileUrl").isEqualTo("https://example.com/mediafile")
            .jsonPath("$[0].task.id").isNumber()
            .jsonPath("$[0].task.name").isEqualTo("Task 1")
            .jsonPath("$[0].project.id").isNumber()
            .jsonPath("$[0].project.name").isEqualTo("Project 2");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ENGINEER", "FINANCE"})
    void findForUser_invalidRole_isForbidden(String role) {
        setupValidToken("pm@example.com", role);
        var userId = createData();

        var startDate = LocalDate.now().minusDays(1);
        var endDate = LocalDate.now().plusDays(1);
        buildClient(port).get()
            .uri("/time-logs?userId={userId}&startDate={startDate}&endDate={endDate}", userId, startDate, endDate)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ENGINEER", "FINANCE", "PM", "HR", "ADMIN"})
    void findForToken_anyRole_isOk(String role ) {
        setupValidToken("engineer@example.com", role);
        createData();

        var startDate = LocalDate.now().minusDays(1);
        var endDate = LocalDate.now().plusDays(1);
        buildClient(port).get()
            .uri("time-logs?startDate={startDate}&endDate={endDate}", startDate, endDate)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].description").isEqualTo("Worked on Task 1")
            .jsonPath("$[0].durationMinutes").isEqualTo(100)
            .jsonPath("$[0].date").isEqualTo(LocalDate.now().toString())
            .jsonPath("$[0].mediaFileUrl").isEqualTo("https://example.com/mediafile")
            .jsonPath("$[0].task.id").isNumber()
            .jsonPath("$[0].task.name").isEqualTo("Task 1")
            .jsonPath("$[0].project.id").isNumber()
            .jsonPath("$[0].project.name").isEqualTo("Project 2");
    }

    @ParameterizedTest
    @ValueSource(strings = {"PM", "HR", "ADMIN"})
    void findForUser_invalidUser_isNotFound(String role) {
        setupValidToken("pm@example.com", role);
        var userId = createData() + 1;

        var startDate = LocalDate.now().minusDays(1);
        var endDate = LocalDate.now().plusDays(1);
        var spec = buildClient(port).get()
            .uri("/time-logs?userId={userId}&startDate={startDate}&endDate={endDate}", userId, startDate, endDate)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateNotFound(spec);
    }

    static List<Arguments> wrongPeriod() {
        return List.of(
            Arguments.of(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)),
            Arguments.of(LocalDate.now().minusDays(2), LocalDate.now().minusDays(1))
        );
    }

    @ParameterizedTest
    @MethodSource("wrongPeriod")
    void findForToken_wrongPeriod_empty(LocalDate startDate, LocalDate endDate) {
        setupValidToken("engineer@example.com");
        createData();

        buildClient(port).get()
            .uri("/time-logs?startDate={startDate}&endDate={endDate}", startDate, endDate)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void findForToken_invalidPeriod_isBadRequest() {
        setupValidToken("engineer@example.com");
        createData();

        var startDate = LocalDate.now().plusDays(1);
        var endDate = LocalDate.now().minusDays(1);
        var spec = buildClient(port).get()
            .uri("/time-logs?startDate={startDate}&endDate={endDate}", startDate, endDate)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateBadRequest(spec);
    }

    @Test
    void findForUser_pmHasNoProjects_isForbidden() {
        setupValidToken("pm-no-projects@example.com", "PM");
        var userId = createData();

        var startDate = LocalDate.now().minusDays(1);
        var endDate = LocalDate.now().plusDays(1);
        buildClient(port).get()
            .uri("/time-logs?userId={userId}&startDate={startDate}&endDate={endDate}", userId, startDate, endDate)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void findForUser_invalidToken_isUnauthorized() {
        setupValidToken("pm@example.com", "PM");
        var userId = createData();

        var startDate = LocalDate.now().minusDays(1);
        var endDate = LocalDate.now().plusDays(1);
        var spec = buildClient(port).get()
            .uri("/time-logs?userId={userId}&startDate={startDate}&endDate={endDate}", userId, startDate, endDate)
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

    @Test
    void findForToken_invalidToken_isUnauthorized() {
        setupValidToken("engineer@example.com");
        createData();

        var startDate = LocalDate.now().minusDays(1);
        var endDate = LocalDate.now().plusDays(1);
        var spec = buildClient(port).get()
            .uri("/time-logs?startDate={startDate}&endDate={endDate}", startDate, endDate)
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

}
