package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.extension.UserHireDateExtension;
import com.ua.teamconnect.tracker.model.entity.Department;
import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.model.entity.UserPosition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(UserHireDateExtension.class)
class UserRepositoryTest extends UserRelatedRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserPositionRepository userPositionRepository;

    @MockitoBean
    @SuppressWarnings("unused") // Need for context not complaining of missing bean
    private JwtDecoder jwtDecoder;

    @AfterEach
    void cleanUp() {
        userPositionRepository.deleteAll();
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void setupData() {
        var user = userRepository.save(newUser());

        var department = new Department();
        department.setName("Software Development");
        department = departmentRepository.save(department);

        var position = new Position();
        position.setName("Software Engineer");
        position.setDepartment(department);
        position = positionRepository.save(position);

        var userPosition = UserPosition.of(user, position);
        userPosition.setStartDate(LocalDate.of(2024, Month.FEBRUARY, 5));
        userPositionRepository.save(userPosition);
    }

    @Test
    void findAnniversaries_existsInOneMonth_notEmptyList() {
        setupData();

        var anniversaries = userRepository.findAnniversaries(2, 1, 2, 20);

        assertEquals(1, anniversaries.size());
        assertEquals("John", anniversaries.get(0).getFirstName());
        assertEquals("Doe", anniversaries.get(0).getLastName());
        assertEquals("https://avatar.com", anniversaries.get(0).getAvatarUrl());
        assertEquals(LocalDate.of(2024, Month.FEBRUARY, 5), anniversaries.get(0).getHireDate());
    }

    @Test
    void findAnniversaries_inDifferentMonth_notEmptyList() {
        setupData();

        var anniversaries = userRepository.findAnniversaries(1, 10, 3, 2);

        assertEquals(1, anniversaries.size());
        assertEquals("John", anniversaries.get(0).getFirstName());
        assertEquals("Doe", anniversaries.get(0).getLastName());
        assertEquals("https://avatar.com", anniversaries.get(0).getAvatarUrl());
        assertEquals(LocalDate.of(2024, Month.FEBRUARY, 5), anniversaries.get(0).getHireDate());
    }

    @Test
    void findAnniversaries_notExistsInOneMonth_emptyList() {
        setupData();

        var anniversaries = userRepository.findAnniversaries(2, 10, 2, 20);

        assertTrue(anniversaries.isEmpty());
    }

    @Test
    void findAnniversaries_afterHireDate_emptyList() {
        setupData();

        var anniversaries = userRepository.findAnniversaries(2, 10, 3, 2);

        assertTrue(anniversaries.isEmpty());
    }

    @Test
    void findAnniversaries_beforeHireDate_emptyList() {
        setupData();

        var anniversaries = userRepository.findAnniversaries(1, 10, 2, 2);

        assertTrue(anniversaries.isEmpty());
    }
}
