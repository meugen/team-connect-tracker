package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.Project;
import com.ua.teamconnect.tracker.model.entity.UserProject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserProjectRepositoryTest extends UserRelatedRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @MockBean
    @SuppressWarnings("unused") // Need for context not complaining of missing bean
    private JwtDecoder jwtDecoder;

    @AfterEach
    void cleanUp() {
        userProjectRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    private Long setupData() {
        var teamConnect = new Project();
        teamConnect.setName("Team Connect");
        teamConnect.setStatus("ACTIVE");
        teamConnect.setStartDate(LocalDate.of(2024, 1, 1));
        teamConnect = projectRepository.save(teamConnect);

        var cloudForge = new Project();
        cloudForge.setName("Cloud Forge");
        cloudForge.setStatus("ACTIVE");
        cloudForge.setStartDate(LocalDate.of(2023, 1, 1));
        cloudForge.setEndDate(LocalDate.of(2026, 1, 1));
        cloudForge = projectRepository.save(cloudForge);

        var user = userRepository.save(newUser());

        var userTeamConnect = UserProject.of(user, teamConnect);
        userTeamConnect.setRole("DEVELOPER");
        userTeamConnect.setStartDate(LocalDate.of(2024, 2, 1));
        userProjectRepository.save(userTeamConnect);

        var userCloudForge = UserProject.of(user, cloudForge);
        userCloudForge.setRole("DEVELOPER");
        userCloudForge.setStartDate(LocalDate.of(2023, 2, 1));
        userProjectRepository.save(userCloudForge);

        return user.getId();
    }

    @Test
    void findByUserIdAndNow_nowBeforeFirstProject_emptyList() {
        var userId = setupData();
        var projects = userProjectRepository.findByUserIdAndNow(userId,
            LocalDate.of(2022, 12, 31)
        );
        assertTrue(projects.isEmpty());
    }

    @Test
    void findByUserIdAndNow_nowBeforeStartAtFirstProject_emptyList() {
        var userId = setupData();
        var projects = userProjectRepository.findByUserIdAndNow(userId,
            LocalDate.of(2023, 1, 10)
        );
        assertTrue(projects.isEmpty());
    }

    @Test
    void findByUserIdAndNow_nowBeforeSecondProject_oneItem() {
        var userId = setupData();
        var projects = userProjectRepository.findByUserIdAndNow(userId,
            LocalDate.of(2023, 2, 10)
        );
        assertEquals(1, projects.size());
        assertEquals("Cloud Forge", projects.get(0).getProject().getName());
    }

    @Test
    void findByUserIdAndNow_nowBeforeStartAtSecondProject_oneItem() {
        var userId = setupData();
        var projects = userProjectRepository.findByUserIdAndNow(userId,
            LocalDate.of(2024, 1, 10)
        );
        assertEquals(1, projects.size());
        assertEquals("Cloud Forge", projects.get(0).getProject().getName());
    }

    @Test
    void findByUserIdAndNow_nowBeforeSecondProjectEnds_twoItem() {
        var userId = setupData();
        var projects = userProjectRepository.findByUserIdAndNow(userId,
            LocalDate.of(2024, 2, 10)
        );
        assertEquals(2, projects.size());
        assertEquals("Team Connect", projects.get(0).getProject().getName());
        assertEquals("Cloud Forge", projects.get(1).getProject().getName());
    }

    @Test
    void findByUserIdAndNow_nowAfterSecondProjectEnds_oneItem() {
        var userId = setupData();
        var projects = userProjectRepository.findByUserIdAndNow(userId,
            LocalDate.of(2026, 2, 10)
        );
        assertEquals(1, projects.size());
        assertEquals("Team Connect", projects.get(0).getProject().getName());
    }

    @Test
    void findByUserIdAndNow_invalidUser_emptyList() {
        var userId = setupData() + 1;
        var projects = userProjectRepository.findByUserIdAndNow(userId,
            LocalDate.of(2024, 2, 10)
        );
        assertTrue(projects.isEmpty());
    }
}
