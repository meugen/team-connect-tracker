package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.Project;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.UserProject;
import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.repository.ProjectRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerTest extends AuthorizationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @AfterEach
    void cleanUp() {
        userProjectRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
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

    private Integer createData(CreateParams params) {
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
        project3.setEndDate(params.getProject3EndDate());
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
        userProject.setEndDate(params.getUserProject3EndDate());
        userProject.setRole("DEVELOPER");
        userProjectRepository.save(userProject);

        return user.getId();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PM", "HR", "ADMIN"})
    void findForUser_validRole_isOk(String role) {
        setupValidToken("pm@example.com", role);
        var userId = createData(CreateParams.allDefaults());

        buildClient(port).get()
            .uri("/projects?userId=" + userId)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].id").isNumber()
            .jsonPath("$[0].name").isEqualTo("Project 2")
            .jsonPath("$[0].description").isEqualTo("Project 2 description")
            .jsonPath("$[1].id").isNumber()
            .jsonPath("$[1].name").isEqualTo("Project 3")
            .jsonPath("$[1].description").isEqualTo("Project 3 description");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ENGINEER", "FINANCE"})
    void findForUser_invalidRole_isForbidden(String role) {
        setupValidToken("pm@example.com", role);
        var userId = createData(CreateParams.allDefaults());

        buildClient(port).get()
            .uri("/projects?userId=" + userId)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PM", "HR", "ADMIN"})
    void findForUser_userNotExists_isOk(String role) {
        setupValidToken("pm@example.com", role);
        var userId = createData(CreateParams.allDefaults()) + 1;

        var spec = buildClient(port).get()
            .uri("/projects?userId=" + userId)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateNotFound(spec);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ENGINEER", "FINANCE", "PM", "HR", "ADMIN"})
    void findForToken_anyRole_isOk(String role) {
        setupValidToken("engineer@example.com", role);
        createData(CreateParams.allDefaults());

        buildClient(port).get()
            .uri("/projects")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].id").isNumber()
            .jsonPath("$[0].name").isEqualTo("Project 2")
            .jsonPath("$[0].description").isEqualTo("Project 2 description")
            .jsonPath("$[1].id").isNumber()
            .jsonPath("$[1].name").isEqualTo("Project 3")
            .jsonPath("$[1].description").isEqualTo("Project 3 description");
    }

    @Test
    void findForUser_pmNoProjects_isForbidden() {
        setupValidToken("pm-no-projects@example.com", "PM");
        var userId = createData(CreateParams.allDefaults());

        buildClient(port).get()
            .uri("/projects?userId=" + userId)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void findForUser_invalidToken_isUnauthorized() {
        setupValidToken("pm@example.com", "PM");
        var userId = createData(CreateParams.allDefaults());

        var spec = buildClient(port).get()
            .uri("/projects?userId=" + userId)
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

    @Test
    void findForToken_invalidToken_isUnauthorized() {
        setupValidToken("engineer@example.com");
        createData(CreateParams.allDefaults());

        var spec = buildClient(port).get()
            .uri("projects")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

    @Test
    void findForToken_project3Ended_isOk() {
        setupValidToken("engineer@example.com");
        var params = CreateParams.builder()
            .project3EndDate(LocalDate.now().minusDays(1))
            .build();
        createData(params);

        buildClient(port).get()
            .uri("/projects")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isNumber()
            .jsonPath("$[0].name").isEqualTo("Project 2")
            .jsonPath("$[0].description").isEqualTo("Project 2 description");
    }

    @Test
    void findForToken_userProject3Ended_isOk() {
        setupValidToken("engineer@example.com");
        var params = CreateParams.builder()
            .userProject3EndDate(LocalDate.now().minusDays(1))
            .build();
        createData(params);

        buildClient(port).get()
            .uri("/projects")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isNumber()
            .jsonPath("$[0].name").isEqualTo("Project 2")
            .jsonPath("$[0].description").isEqualTo("Project 2 description");
    }

    @Builder @Getter
    private static class CreateParams {

        public static CreateParams allDefaults() { return CreateParams.builder().build(); }

        @Builder.Default
        private LocalDate project3EndDate =  null;
        @Builder.Default
        private LocalDate userProject3EndDate =  null;
    }
}
