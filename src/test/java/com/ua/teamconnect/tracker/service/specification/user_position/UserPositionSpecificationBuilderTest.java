package com.ua.teamconnect.tracker.service.specification.user_position;

import com.ua.teamconnect.tracker.model.entity.*;
import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.repository.*;
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
public class UserPositionSpecificationBuilderTest {

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

    private UserData setupUser() {
        var johnUser = new User();
        johnUser.setStatus("ACTIVE");
        johnUser.setGender(Gender.MALE);
        johnUser.setFirstName("John");
        johnUser.setLastName("Doe");
        johnUser.setEmail("john.doe@example.com");
        johnUser.setPhone(Map.of());
        johnUser.setPassword("password");
        johnUser.setAvatar("avatar.png");
        johnUser.setBirthDate(LocalDate.now());
        johnUser.setGrade("Senior");
        johnUser.setRole("ENGINEER");
        johnUser = userRepository.save(johnUser);

        var johnDepartment = new Department();
        johnDepartment.setName("Software Development");
        johnDepartment = departmentRepository.save(johnDepartment);

        var  johnPosition = new Position();
        johnPosition.setDepartment(johnDepartment);
        johnPosition.setName("Software Engineer");
        johnPosition= positionRepository.save(johnPosition);

        var johnUserPosition = UserPosition.of(johnUser, johnPosition);
        johnUserPosition.setStartDate(LocalDate.of(2024, Month.FEBRUARY, 10));
        userPositionRepository.save(johnUserPosition);

        var johnStack = new Stack();
        johnStack.setName("Java");
        johnStack = stackRepository.save(johnStack);

        var johnUserStack = UserStack.of(johnUser, johnStack);
        johnUserStack.setIsPrimary(true);
        userStackRepository.save(johnUserStack);

        var ellisonUser = new User();
        ellisonUser.setStatus("ACTIVE");
        ellisonUser.setGender(Gender.FEMALE);
        ellisonUser.setFirstName("Ellison");
        ellisonUser.setLastName("Smith");
        ellisonUser.setEmail("ellison.smith@example.com");
        ellisonUser.setPhone(Map.of());
        ellisonUser.setPassword("password");
        ellisonUser.setAvatar("avatar.png");
        ellisonUser.setBirthDate(LocalDate.now());
        ellisonUser.setGrade("Senior");
        ellisonUser.setRole("ENGINEER");
        ellisonUser = userRepository.save(ellisonUser);

        var ellisonDepartment = new Department();
        ellisonDepartment.setName("HR Department");
        ellisonDepartment = departmentRepository.save(ellisonDepartment);

        var  ellisonPosition = new Position();
        ellisonPosition.setDepartment(ellisonDepartment);
        ellisonPosition.setName("Recruitment");
        ellisonPosition = positionRepository.save(ellisonPosition);

        var ellisonUserPosition = UserPosition.of(ellisonUser, ellisonPosition);
        ellisonUserPosition.setStartDate(LocalDate.of(2024, Month.MARCH, 10));
        userPositionRepository.save(ellisonUserPosition);

        var ellisonStack = new Stack();
        ellisonStack.setName("Human Resources");
        ellisonStack = stackRepository.save(ellisonStack);

        var ellisonUserStack = UserStack.of(ellisonUser, ellisonStack);
        ellisonUserStack.setIsPrimary(true);
        userStackRepository.save(ellisonUserStack);

        return new UserData(ellisonPosition.getId(), ellisonDepartment.getId(), ellisonStack.getId());
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
        setupUser();

        var pair = builder.build(Map.of());
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(2, result.size());
        validateJohn(result.get(0));
        validateEllison(result.get(1));
    }

    @Test
    void build_withDepartmentParam_validResult() {
        var data = setupUser();

        var pair = builder.build(Map.of("department", data.departmentId().toString()));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(1, result.size());
        validateEllison(result.get(0));
    }

    @Test
    void build_withPositionParam_validResult() {
        var data = setupUser();

        var pair = builder.build(Map.of("position", data.positionId().toString()));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(1, result.size());
        validateEllison(result.get(0));
    }

    @Test
    void build_withStackParam_validResult() {
        var data = setupUser();

        var pair = builder.build(Map.of("stack", data.stackId().toString()));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(1, result.size());
        validateEllison(result.get(0));
    }

    @Test
    void build_withSearch_validResult() {
        setupUser();

        var pair = builder.build(Map.of("search", "john"));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(1, result.size());
        validateJohn(result.get(0));
    }

    @Test
    void build_invalidSearch_validResult() {
        setupUser();

        var pair = builder.build(Map.of("search", "team"));
        var result = userPositionRepository.findAll(pair.first());

        assertEquals(0, result.size());
    }

    private record UserData(
        Integer positionId,
        Integer departmentId,
        Integer stackId
    ) {}
}
