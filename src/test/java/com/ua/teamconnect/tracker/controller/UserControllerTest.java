package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.extension.UserHireDateExtension;
import com.ua.teamconnect.tracker.model.entity.*;
import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends AuthorizationControllerTest {

    private static final String ROLE_EMPLOYEE = "EMPLOYEE";
    private static final String ROLE_ADMIN = "ADMIN";

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

    private UserData setupUser(String role) {
        var user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setAvatar("https://avatar.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(role);
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
        userProject.setStartDate(LocalDate.of(2023, Month.MARCH, 1));
        userProjectRepository.save(userProject);

        var userPosition = UserPosition.of(user, position);
        userPosition.setStartDate(LocalDate.of(2023, Month.FEBRUARY, 1));
        userPositionRepository.save(userPosition);

        var params = Map.of(
            "department", department.getId().toString(),
            "position", position.getId().toString(),
            "stack", stack.getId().toString(),
            "search", "john",
            "size", "10",
            "page", "1",
            "sort", "firstName",
            "order", "desc"
        );
        return new UserData(user.getId(), position.getId(), department.getId(), stack.getId(), params);
    }

    private void profileIsValid(WebTestClient.BodyContentSpec spec, boolean full) {
        spec.jsonPath("$.id").isNumber()
            .jsonPath("$.firstName").isEqualTo("John")
            .jsonPath("$.lastName").isEqualTo("Doe")
            .jsonPath("$.avatar").isEqualTo("https://avatar.com")
            .jsonPath("$.workEmail").isEqualTo("user@example.com")
            .jsonPath("$.hireDate").isEqualTo("2023-02-01")
            .jsonPath("$.grade").isEqualTo("SENIOR")
            .jsonPath("$.gender").isEqualTo("MALE").jsonPath("$.stacks").isArray()
            .jsonPath("$.stacks[0].id").isNumber()
            .jsonPath("$.stacks[0].name").isEqualTo("Java")
            .jsonPath("$.positions").isArray()
            .jsonPath("$.positions[0].id").isNumber()
            .jsonPath("$.positions[0].name").isEqualTo("Java Developer")
            .jsonPath("$.positions[0].department.id").isNumber()
            .jsonPath("$.positions[0].department.name").isEqualTo("Software Development")
            .jsonPath("$.projects").isArray()
            .jsonPath("$.projects[0].id").isNumber()
            .jsonPath("$.projects[0].name").isEqualTo("Team Connect");
        if (full) {
            spec.jsonPath("$.phones.home").isEqualTo("+123456789")
                .jsonPath("$.phones.mobile").isEqualTo("+987654321")
                .jsonPath("$.birthDate").isEqualTo("1990-05-10");
        }
    }

    @Test
    void profile_validToken_isOk() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken("user@example.com");

        var spec = buildClient(port).get()
            .uri("/users/profile")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody();
        profileIsValid(spec, true);
    }

    @Test
    void profile_invalidToken_isUnauthorized() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/users/profile")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

    @Test
    @ExtendWith(UserHireDateExtension.class)
    void getAnniversariesBetween_validTokenAndRequest_isOkAndNotEmpty() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        buildClient(port).get()
            .uri("/users/anniversaries?startDate=20-01&endDate=10-02")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isNumber()
            .jsonPath("$[0].firstName").isEqualTo("John")
            .jsonPath("$[0].lastName").isEqualTo("Doe")
            .jsonPath("$[0].avatarUrl").isEqualTo("https://avatar.com")
            .jsonPath("$[0].hireDate").isEqualTo("2023-02-01");
    }

    @Test
    @ExtendWith(UserHireDateExtension.class)
    void getAnniversariesBetween_validTokenAndAfterHireDate_isOkAndEmpty() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        buildClient(port).get()
            .uri("/users/anniversaries?startDate=10-02&endDate=20-02")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    @ExtendWith(UserHireDateExtension.class)
    void getAnniversariesBetween_validTokenAndBeforeHireDate_isOkAndEmpty() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        buildClient(port).get()
            .uri("/users/anniversaries?startDate=10-01&endDate=20-01")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void getAnniversariesBetween_invalidToken_isUnauthorized() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/users/anniversaries?startDate=20-01&endDate=10-02")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

    @Test
    @ExtendWith(UserHireDateExtension.class)
    void getAnniversariesBetween_startAfterEnd_isOkAndNotEmpty() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        buildClient(port).get()
            .uri("/users/anniversaries?startDate=20-12&endDate=10-02")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isNumber()
            .jsonPath("$[0].firstName").isEqualTo("John")
            .jsonPath("$[0].lastName").isEqualTo("Doe")
            .jsonPath("$[0].avatarUrl").isEqualTo("https://avatar.com")
            .jsonPath("$[0].hireDate").isEqualTo("2023-02-01");
    }

    @Test
    void getAnniversariesBetween_invalidStart_isBadRequest() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/users/anniversaries?startDate=201-01&endDate=10-02")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateBadRequest(spec);
    }

    @Test
    void getAnniversariesBetween_invalidEnd_isBadRequest() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/users/anniversaries?startDate=20-01&endDate=101-02")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateBadRequest(spec);
    }

    @Test
    void getAnniversariesBetween_noStart_isBadRequest() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/users/anniversaries?endDate=10-02")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateBadRequest(spec);
    }

    @Test
    void getAnniversariesBetween_noEnd_isBadRequest() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/users/anniversaries?startDate=20-01")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateBadRequest(spec);
    }

    @Test
    void getUserById_roleEmployee_isOkShort() {
        var userId = setupUser(ROLE_EMPLOYEE).userId();
        setupValidToken("user@example.com");

        var spec = buildClient(port).get()
            .uri("/users/" + userId)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody();
        profileIsValid(spec, false);
    }

    @Test
    void getUserById_roleAdmin_isOkFull() {
        var userId = setupUser(ROLE_ADMIN).userId();
        setupValidToken("user@example.com");

        var spec = buildClient(port).get()
            .uri("/users/" + userId)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody();
        profileIsValid(spec, true);
    }

    @Test
    void getUserById_invalidToken_isUnauthorized() {
        var userId = setupUser(ROLE_EMPLOYEE).userId();
        setupValidToken("user@example.com");

        var spec = buildClient(port).get()
            .uri("/users/" + userId)
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

    @Test
    void getUserById_invalidUser_isNotFound() {
        var userId = setupUser(ROLE_EMPLOYEE).userId() + 1;
        setupValidToken("user@example.com");

        var spec = buildClient(port).get()
            .uri("/users/" + userId)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateNotFound(spec);
    }

    private void validateFilteredUsers(WebTestClient.BodyContentSpec spec, UserData user, boolean empty) {
        spec.jsonPath("$.items").isArray()
            .jsonPath("$.items.length()").isEqualTo(empty ? 0 : 1)
            .jsonPath("$.totalPages").isEqualTo(empty ? 0 : 1)
            .jsonPath("$.currentPage").isEqualTo(1)
            .jsonPath("$.totalItems").isEqualTo(empty ?  0 : 1);
        if (!empty) {
            spec.jsonPath("$.items[0].id").isEqualTo(user.userId())
                .jsonPath("$.items[0].firstName").isEqualTo("John")
                .jsonPath("$.items[0].lastName").isEqualTo("Doe")
                .jsonPath("$.items[0].avatarUrl").isEqualTo("https://avatar.com")
                .jsonPath("$.items[0].position.id").isEqualTo(user.positionId())
                .jsonPath("$.items[0].position.name").isEqualTo("Java Developer")
                .jsonPath("$.items[0].position.department.id").isEqualTo(user.departmentId())
                .jsonPath("$.items[0].position.department.name").isEqualTo("Software Development");
        }
    }

    @Test
    void findFiltered_noParams_isOkAndNonEmpty() {
        var user = setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var spec = buildClient(port).get()
            .uri("/users")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody();
        validateFilteredUsers(spec, user, false);
    }

    @Test
    void findFiltered_validParams_isOkAndNonEmpty() {
        var user = setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var query = user.params().entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
        var spec = buildClient(port).get()
            .uri("/users?" + query)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody();
        validateFilteredUsers(spec, user, false);
    }

    static List<Arguments> invalidParams() {
        return List.of(
            Arguments.of("department", "abc"),
            Arguments.of("position", "abc"),
            Arguments.of("stack", "abc"),
            Arguments.of("size", "abc"),
            Arguments.of("page", "abc"),
            Arguments.of("sort", "abc"),
            Arguments.of("order", "abc")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidParams")
    void findFiltered_invalidParam_isBadRequest(String name, String value) {
        var user = setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var params = new HashMap<>(user.params());
        params.put(name, value);
        var query = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
        var spec = buildClient(port).get()
            .uri("/users?" + query)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange();
        validateBadRequest(spec);
    }

    @Test
    void findFiltered_departmentValidButNotExists_isOkAndEmpty() {
        var user = setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var params = new HashMap<>(user.params());
        params.put("department", Integer.toString(user.departmentId() + 1));
        var query = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
        var spec = buildClient(port).get()
            .uri("/users?" + query)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody();
        validateFilteredUsers(spec, user, true);
    }

    @Test
    void findFiltered_positionValidButNotExists_isOkAndEmpty() {
        var user = setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var params = new HashMap<>(user.params());
        params.put("position", Integer.toString(user.positionId() + 1));
        var query = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
        var spec = buildClient(port).get()
            .uri("/users?" + query)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody();
        validateFilteredUsers(spec, user, true);
    }

    @Test
    void findFiltered_stackValidButNotExists_isOkAndEmpty() {
        var user = setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var params = new HashMap<>(user.params());
        params.put("stack", Integer.toString(user.stackId() + 1));
        var query = params.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
        var spec = buildClient(port).get()
            .uri("/users?" + query)
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody();
        validateFilteredUsers(spec, user, true);
    }

    @Test
    void findFiltered_invalidToken_isUnauthorized() {
        var user = setupUser(ROLE_EMPLOYEE);
        setupValidToken();

        var query = user.params().entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
        var spec = buildClient(port).get()
            .uri("/users?" + query)
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .exchange();
        validateUnauthorized(spec);
    }

    private record UserData(
        Integer userId,
        Integer positionId,
        Integer departmentId,
        Integer stackId,
        Map<String, String> params
    ) {
    }
    
    @Test
    void updateProfile_updatedProfile_isNoContent() {
        setupUser(ROLE_ADMIN);
        setupValidToken("user@example.com");
        var bodyValue = """
                        {
                        "avatar": "https://new-avatar.com",
                        "phone": {
                          "work": "+380697554332",
                          "home": "+380441234567"
                        },
                        "password": "new_password"
                      }
                      """;
        
        var spec = buildClient(port).put()
            .uri("/users/profile")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(bodyValue)
            .exchange();

        var updatedUser = userRepository.findByEmail("user@example.com").orElseThrow();
        validateNoContent(spec);
        assertEquals("https://new-avatar.com", updatedUser.getAvatar());
        assertEquals("+380697554332", updatedUser.getPhone().get("work"));
        assertEquals("+380441234567", updatedUser.getPhone().get("home"));
    }
    
    @Test
    void updateProfile_invalidToken_isUnauthorized() {
        setupUser(ROLE_ADMIN);
        setupValidToken("user@example.com");
        var bodyValue = """
                        {
                        "avatar": "https://new-avatar.com"
                      }
                      """;

        var spec = buildClient(port).put()
            .uri("/users/profile")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(bodyValue)
            .exchange();

        validateUnauthorized(spec);
    }
    
    @Test
    void updateProfile_invalidPassword_isBadRequest() {
        setupUser(ROLE_EMPLOYEE);
        setupValidToken("user@example.com");
        var bodyValue = """
                        {
                        "password": "12"
                      }
                      """;
        
        var spec = buildClient(port).put()
            .uri("/users/profile")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(bodyValue)
            .exchange();
        spec.expectStatus().isBadRequest();
    }

}
