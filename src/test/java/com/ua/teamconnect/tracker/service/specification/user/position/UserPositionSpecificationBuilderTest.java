package com.ua.teamconnect.tracker.service.specification.user.position;

import com.ua.teamconnect.tracker.model.entity.*;
import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.repository.*;
import com.ua.teamconnect.tracker.repository.specification.user.position.UserPositionSpecificationBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserPositionSpecificationBuilderTest {

    private static final UserData JOHN_DATA = new UserData(
        "John", "Doe", Gender.MALE, "jon.doe@example.com",
        "Software Engineer", "Software Development", "Java",
        LocalDate.of(2024, Month.FEBRUARY, 10)
    );
    private static final UserData ELLISON_DATA = new UserData(
        "Ellison", "Smith", Gender.FEMALE, "ellison.smith@example.com",
        "Recruitment", "HR Department", "Human Resources",
        LocalDate.of(2024, Month.MARCH, 10)
    );

    private final UserPositionSpecificationBuilder builder = new UserPositionSpecificationBuilder();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private UserPositionRepository userPositionRepository;
    @Autowired
    private UserStackRepository userStackRepository;
    @Autowired
    private StackRepository stackRepository;

    @MockitoBean
    @SuppressWarnings("unused") // Need for context not complaining of missing bean
    private JwtDecoder jwtDecoder;

    @AfterEach
    void deleteAll() {
        userPositionRepository.deleteAll();
        userStackRepository.deleteAll();
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
        stackRepository.deleteAll();
        userRepository.deleteAll();
    }

    private UserIdentifiers setupUser(UserData data) {
        var user = new User();
        user.setStatus("ACTIVE");
        user.setGender(data.gender());
        user.setFirstName(data.firstName());
        user.setLastName(data.lastName());
        user.setEmail(data.email());
        user.setPhone(Map.of());
        user.setPassword("password");
        user.setAvatar("avatar.png" + data.email);
        user.setBirthDate(LocalDate.now());
        user.setGrade("Senior");
        user.setRole("ENGINEER");
        user = userRepository.save(user);

        var department = new Department();
        department.setName(data.departmentName());
        department = departmentRepository.save(department);

        var  position = new Position();
        position.setDepartment(department);
        position.setName(data.positionName());
        position= positionRepository.save(position);

        var userPosition = UserPosition.of(user, position);
        userPosition.setStartDate(data.startDate());
        userPositionRepository.save(userPosition);

        var stack = new Stack();
        stack.setName(data.stackName());
        stack = stackRepository.save(stack);

        var userStack = UserStack.of(user, stack);
        userStack.setIsPrimary(true);
        userStackRepository.save(userStack);

        return new UserIdentifiers(position.getId(), department.getId(), stack.getId());
    }

    private void validateEllison(UserPosition userPosition) {
        assertEquals("Ellison", userPosition.getUser().getFirstName());
        assertEquals("Smith", userPosition.getUser().getLastName());
        assertEquals("HR Department", userPosition.getPosition().getDepartment().getName());
        assertEquals("Recruitment", userPosition.getPosition().getName());
    }

    private void validateJohn(UserPosition userPosition) {
        assertEquals("John", userPosition.getUser().getFirstName());
        assertEquals("Doe", userPosition.getUser().getLastName());
        assertEquals("Software Development", userPosition.getPosition().getDepartment().getName());
        assertEquals("Software Engineer", userPosition.getPosition().getName());
    }

    @Test
    void build_withoutParams_validResult() {
        setupUser(JOHN_DATA);
        setupUser(ELLISON_DATA);

        var pair = builder.build(Map.of());
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(2, result.size());
        validateJohn(result.get(0));
        validateEllison(result.get(1));
    }

    @Test
    void build_withDepartmentParam_validResult() {
        setupUser(JOHN_DATA);
        var ids = setupUser(ELLISON_DATA);

        var pair = builder.build(Map.of("department", ids.departmentId().toString()));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(1, result.size());
        validateEllison(result.get(0));
    }

    @Test
    void build_withPositionParam_validResult() {
        setupUser(JOHN_DATA);
         var ids = setupUser(ELLISON_DATA);

        var pair = builder.build(Map.of("position", ids.positionId().toString()));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(1, result.size());
        validateEllison(result.get(0));
    }

    @Test
    void build_withStackParam_validResult() {
        setupUser(JOHN_DATA);
        var ids = setupUser(ELLISON_DATA);

        var pair = builder.build(Map.of("stack", ids.stackId().toString()));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(1, result.size());
        validateEllison(result.get(0));
    }

    @Test
    void build_withSearch_validResult() {
        setupUser(JOHN_DATA);
        setupUser(ELLISON_DATA);

        var pair = builder.build(Map.of("search", "john"));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(1, result.size());
        validateJohn(result.get(0));
    }

    @Test
    void build_invalidSearch_validResult() {
        setupUser(JOHN_DATA);
        setupUser(ELLISON_DATA);

        var pair = builder.build(Map.of("search", "tim"));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(0, result.size());
    }

    private record UserData(
        String firstName,
        String lastName,
        Gender gender,
        String email,
        String positionName,
        String departmentName,
        String stackName,
        LocalDate startDate
    ) {}

    private record UserIdentifiers(
        Integer positionId,
        Integer departmentId,
        Integer stackId
    ) {}
}
