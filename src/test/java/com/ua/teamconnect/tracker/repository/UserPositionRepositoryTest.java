package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.Department;
import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.model.entity.UserPosition;
import com.ua.teamconnect.tracker.service.adapter.storage.StorageAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserPositionRepositoryTest extends UserRelatedRepositoryTest {

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

    @MockitoBean
    @SuppressWarnings("unused") // Need for context not complaining of missing bean
    private StorageAdapter storageAdapter;

    @AfterEach
    void cleanUp() {
        userPositionRepository.deleteAll();
        positionRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Integer setupData() {
        var department = new Department();
        department.setName("Software Development");
        department = departmentRepository.save(department);

        var javaDeveloper = new Position();
        javaDeveloper.setName("Java Developer");
        javaDeveloper.setDepartment(department);
        javaDeveloper = positionRepository.save(javaDeveloper);

        var devops = new Position();
        devops.setName("DevOps");
        devops.setDepartment(department);
        devops = positionRepository.save(devops);

        var user = userRepository.save(newUser());

        var userJavaDeveloper = UserPosition.of(user, javaDeveloper);
        userJavaDeveloper.setStartDate(LocalDate.of(2020, Month.JANUARY, 1));
        userPositionRepository.save(userJavaDeveloper);

        var userDevops = UserPosition.of(user, devops);
        userDevops.setStartDate(LocalDate.of(2021, Month.JANUARY, 1));
        userPositionRepository.save(userDevops);

        return user.getId();
    }

    @Test
    void findByUserIdAndNow_nowBeforeFirstPosition_emptyList() {
        var userId = setupData();
        var positions = userPositionRepository.findByUserIdAndNow(userId,
            LocalDate.of(2019, Month.DECEMBER, 31)
        );
        assertTrue(positions.isEmpty());
    }

    @Test
    void findByUserIdAndNow_nowBeforeSecondPosition_singleItem() {
        var userId = setupData();
        var positions = userPositionRepository.findByUserIdAndNow(userId,
            LocalDate.of(2020, Month.DECEMBER, 31)
        );
        assertEquals(1, positions.size());
        assertEquals("Java Developer", positions.get(0).getPosition().getName());
    }

    @Test
    void findByUserIdAndNow_nowAfterSecondPosition_twoItems() {
        var userId = setupData();
        var positions = userPositionRepository.findByUserIdAndNow(userId,
            LocalDate.of(2021, Month.DECEMBER, 31)
        );
        assertEquals(2, positions.size());
        assertEquals("Java Developer", positions.get(0).getPosition().getName());
        assertEquals("DevOps", positions.get(1).getPosition().getName());
    }

    @Test
    void findByUserIdAndNow_invalidUser_emptyList() {
        var userId = setupData() + 1;
        var positions = userPositionRepository.findByUserIdAndNow(userId,
            LocalDate.of(2021, Month.DECEMBER, 31)
        );
        assertTrue(positions.isEmpty());
    }

    @Test
    void findHireDateByUserId_validUser_validHireDate() {
        var userId = setupData();
        var hireDate = userPositionRepository.findHireDateByUserId(userId);
        assertTrue(hireDate.isPresent());
        assertEquals(LocalDate.of(2020, Month.JANUARY, 1), hireDate.get());
    }

    @Test
    void findHireDateByUserId_invalidUser_emptyList() {
        var userId = setupData() + 1;
        var hireDate = userPositionRepository.findHireDateByUserId(userId);
        assertTrue(hireDate.isEmpty());
    }
}
