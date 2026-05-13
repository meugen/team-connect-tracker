package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserAnniversaryMapper;
import com.ua.teamconnect.tracker.mapper.UserProfileMapper;
import com.ua.teamconnect.tracker.model.dto.*;
import com.ua.teamconnect.tracker.model.entity.*;
import com.ua.teamconnect.tracker.model.entity.projection.Anniversary;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.UserStackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private static final Random RANDOM = new Random();

    private UserRepository userRepository;
    private UserPositionRepository userPositionRepository;
    private UserStackRepository userStackRepository;
    private UserProjectRepository userProjectRepository;
    private UserService userService;

    @BeforeEach
    void setupService() {
        userRepository = mock(UserRepository.class);
        userPositionRepository = mock(UserPositionRepository.class);
        userStackRepository = mock(UserStackRepository.class);
        userProjectRepository = mock(UserProjectRepository.class);
        userService = new UserService(
            userRepository,
            userPositionRepository,
            userProjectRepository,
            userStackRepository,
            Mappers.getMapper(UserProfileMapper.class),
            Mappers.getMapper(UserAnniversaryMapper.class)
        );
    }

    @Test
    void profile_repositoryReturnsEntity_returnsDto() {
        var userId = RANDOM.nextInt();
        var user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");
        when(user.getEmail()).thenReturn("user@example.com");
        when(user.getAvatar()).thenReturn("https://avatar.com/");
        when(user.getGrade()).thenReturn("Senior");
        when(user.getPhone()).thenReturn(Map.of(
            "home", "+123456789",
            "mobile", "+987654321"
        ));
        when(user.getBirthDate()).thenReturn(LocalDate.of(1990, Month.APRIL, 21));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        var projectId = RANDOM.nextInt();
        var project = mock(Project.class);
        when(project.getId()).thenReturn(projectId);
        when(project.getName()).thenReturn("TeamConnect");
        var userProject = mock(UserProject.class);
        when(userProject.getProject()).thenReturn(project);
        when(userProject.getStartDate()).thenReturn(LocalDate.of(2020, Month.SEPTEMBER, 2));
        when(userProjectRepository.findByUserIdAndNow(eq(userId), any())).thenReturn(List.of(userProject));

        var departmentId = RANDOM.nextInt();
        var department = mock(Department.class);
        when(department.getId()).thenReturn(departmentId);
        when(department.getName()).thenReturn("Software Development");
        var positionId = RANDOM.nextInt();
        var position = mock(Position.class);
        var userPosition = mock(UserPosition.class);
        when(position.getId()).thenReturn(positionId);
        when(position.getName()).thenReturn("Java Developer");
        when(position.getDepartment()).thenReturn(department);
        when(userPosition.getPosition()).thenReturn(position);
        when(userPositionRepository.findByUserIdAndNow(eq(userId), any())).thenReturn(List.of(userPosition));

        var stackId = RANDOM.nextInt();
        var userStack = mock(UserStack.class);
        var stack = mock(Stack.class);
        when(stack.getId()).thenReturn(stackId);
        when(stack.getName()).thenReturn("Java");
        when(userStack.getStack()).thenReturn(stack);
        when(userStackRepository.findByUserId(userId)).thenReturn(List.of(userStack));

        var hireDate = mock(LocalDate.class);
        when(userPositionRepository.findHireDateByUserId(userId)).thenReturn(Optional.of(hireDate));

        var result = userService.profile("user@example.com");

        var expected = new UserProfileDto(
            userId,
            "John",
            "Doe",
            "https://avatar.com/",
            "user@example.com",
            hireDate,
            "Senior",
            Map.of(
                "home", "+123456789",
                "mobile", "+987654321"
            ),
            List.of(
                new StackDto(stackId, "Java")
            ),
            List.of(
                new ProfilePositionDto(
                    positionId,
                    "Java Developer",
                    new ProfileDepartmentDto(departmentId, "Software Development")
                )
            ),
            List.of(
                new ProfileProjectDto(
                    projectId,
                    "TeamConnect",
                    LocalDate.of(2020, Month.SEPTEMBER, 2)
                )
            ),
            LocalDate.of(1990, Month.APRIL, 21)
        );
        assertEquals(expected, result);
    }

    @Test
    void profile_repositoryReturnsEmpty_throwsException() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.profile("user@example.com"));
    }

    @Test
    void anniversaries_repositoryReturnsEntity_returnsDto() {
        var userId = RANDOM.nextInt();
        var anniversary = mock(Anniversary.class);
        when(anniversary.getUserId()).thenReturn(userId);
        when(anniversary.getFirstName()).thenReturn("John");
        when(anniversary.getLastName()).thenReturn("Doe");
        when(anniversary.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(anniversary.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(userRepository.findAnniversaries(2, 1, 2, 20))
            .thenReturn(List.of(anniversary));

        var actual = userService.anniversaries("01-02", "20-02");

        var expected = List.of(new UserAnniversaryDto(
            userId,
            "John",
            "Doe",
            "https://avatar.com/john_doe.png",
            LocalDate.of(2020, Month.FEBRUARY, 15)
        ));
        assertEquals(expected, actual);
    }

    @Test
    void anniversaries_repositoryReturnsEmpty_returnsDto() {
        when(userRepository.findAnniversaries(2, 1, 2, 20))
            .thenReturn(List.of());

        var actual = userService.anniversaries("01-02", "20-02");

        assertEquals(List.of(), actual);
    }

    @Test
    void anniversaries_singleDate_returnsDto() {
        var userId = RANDOM.nextInt();
        var anniversary = mock(Anniversary.class);
        when(anniversary.getUserId()).thenReturn(userId);
        when(anniversary.getFirstName()).thenReturn("John");
        when(anniversary.getLastName()).thenReturn("Doe");
        when(anniversary.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(anniversary.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(userRepository.findAnniversaries(2, 15, 2, 15))
            .thenReturn(List.of(anniversary));

        var actual = userService.anniversaries("15-02", "15-02");

        var expected = List.of(new UserAnniversaryDto(
            userId,
            "John",
            "Doe",
            "https://avatar.com/john_doe.png",
            LocalDate.of(2020, Month.FEBRUARY, 15)
        ));
        assertEquals(expected, actual);
    }

    @Test
    void anniversaries_startAfterEnd_returnsDto() {
        var johnUserId = RANDOM.nextInt();
        var johnAnniversary = mock(Anniversary.class);
        when(johnAnniversary.getUserId()).thenReturn(johnUserId);
        when(johnAnniversary.getFirstName()).thenReturn("John");
        when(johnAnniversary.getLastName()).thenReturn("Doe");
        when(johnAnniversary.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(johnAnniversary.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));

        var ellisonUserId = RANDOM.nextInt();
        var ellisonAnniversary = mock(Anniversary.class);
        when(ellisonAnniversary.getUserId()).thenReturn(ellisonUserId);
        when(ellisonAnniversary.getFirstName()).thenReturn("Ellison");
        when(ellisonAnniversary.getLastName()).thenReturn("Smith");
        when(ellisonAnniversary.getAvatarUrl()).thenReturn("https://avatar.com/ellison_smith.png");
        when(ellisonAnniversary.getHireDate()).thenReturn(LocalDate.of(2020, Month.DECEMBER, 20));

        when(userRepository.findAnniversaries(1, 1, 2, 20))
            .thenReturn(List.of(johnAnniversary));
        when(userRepository.findAnniversaries(12, 10, 12, 31))
            .thenReturn(List.of(ellisonAnniversary));

        var actual = userService.anniversaries("10-12", "20-02");

        var expected = List.of(
            new UserAnniversaryDto(
                ellisonUserId,
                "Ellison",
                "Smith",
                "https://avatar.com/ellison_smith.png",
                LocalDate.of(2020, Month.DECEMBER, 20)
            ),
            new UserAnniversaryDto(
                johnUserId,
                "John",
                "Doe",
                "https://avatar.com/john_doe.png",
                LocalDate.of(2020, Month.FEBRUARY, 15)
            )
        );
        assertEquals(expected, actual);
    }
}
