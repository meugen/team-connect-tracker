package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.entity.*;
import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends AuthorizationControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StackRepository stackRepository;

    @Autowired
    private UserStackRepository userStackRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserPositionRepository userPositionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @LocalServerPort
    private int port;

    @AfterEach
    void cleanUp() {
        userStackRepository.deleteAll();
        userPositionRepository.deleteAll();
        userProjectRepository.deleteAll();
        projectRepository.deleteAll();
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
        stackRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void setupUser() {
        var user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setAvatar("https://avatar.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("ENGINEER");
        user.setStatus("ACTIVE");
        user.setPhone(Map.of(
            "home", "+123456789",
            "mobile", "+987654321"
        ));
        user.setBirthDate(LocalDate.of(1990, Month.MAY, 10));
        user.setGender(Gender.MALE);
        user.setGrade("SENIOR");
        user = userRepository.save(user);

        var stack = new Stack();
        stack.setName("Java");
        stack = stackRepository.save(stack);

        var department = new Department();
        department.setName("Software Development");
        department = departmentRepository.save(department);

        var position = new Position();
        position.setName("Java Developer");
        position.setDepartment(department);
        position = positionRepository.save(position);

        var project = new Project();
        project.setName("Team Connect");
        project.setStatus("ACTIVE");
        project.setDescription("Project description");
        project.setStartDate(LocalDate.of(2022, Month.DECEMBER, 1));
        project.setIsBillable(false);
        project = projectRepository.save(project);

        var userStack = UserStack.of(user, stack);
        userStack.setIsPrimary(true);
        userStackRepository.save(userStack);

        var userProject = UserProject.of(user, project);
        userProject.setRole("DEVELOPER");
        userProject.setStartDate(LocalDate.of(2023, Month.FEBRUARY, 1));
        userProjectRepository.save(userProject);

        var userPosition = UserPosition.of(user, position);
        userPosition.setStartDate(LocalDate.of(2023, Month.JANUARY, 1));
        userPositionRepository.save(userPosition);
    }

    @Test
    void profile_validToken_isOk() {
        setupUser();
        setupValidToken("user@example.com");

        buildClient(port).get()
            .uri("/users/profile")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isNumber()
            .jsonPath("$.firstName").isEqualTo("John")
            .jsonPath("$.lastName").isEqualTo("Doe")
            .jsonPath("$.avatar").isEqualTo("https://avatar.com")
            .jsonPath("$.workEmail").isEqualTo("user@example.com")
            .jsonPath("$.hireDate").isEqualTo("2023-01-01")
            .jsonPath("$.grade").isEqualTo("SENIOR")
            .jsonPath("$.phones.home").isEqualTo("+123456789")
            .jsonPath("$.phones.mobile").isEqualTo("+987654321")
            .jsonPath("$.stacks").isArray()
            .jsonPath("$.stacks[0].id").isNumber()
            .jsonPath("$.stacks[0].name").isEqualTo("Java")
            .jsonPath("$.positions").isArray()
            .jsonPath("$.positions[0].id").isNumber()
            .jsonPath("$.positions[0].name").isEqualTo("Java Developer")
            .jsonPath("$.positions[0].department.id").isNumber()
            .jsonPath("$.positions[0].department.name").isEqualTo("Software Development")
            .jsonPath("$.projects").isArray()
            .jsonPath("$.projects[0].id").isNumber()
            .jsonPath("$.projects[0].name").isEqualTo("Team Connect")
            .jsonPath("$.birthDate").isEqualTo("1990-05-10");
    }

    @Test
    void profile_invalidToken_isUnauthorized() {
        setupUser();
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/users/profile")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }
}
