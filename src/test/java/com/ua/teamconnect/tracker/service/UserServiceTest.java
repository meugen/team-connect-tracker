package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserAnniversaryMapper;
import com.ua.teamconnect.tracker.mapper.UserProfileMapper;
import com.ua.teamconnect.tracker.model.dto.*;
import com.ua.teamconnect.tracker.model.entity.*;
import com.ua.teamconnect.tracker.model.entity.projection.Anniversary;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.UserStackRepository;
import com.ua.teamconnect.tracker.service.strategy.user_profile.MapFullUserProfileStrategy;
import com.ua.teamconnect.tracker.service.strategy.user_profile.MapShortUserProfileStrategy;
import com.ua.teamconnect.tracker.service.strategy.user_profile.MapUserProfileFactory;
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
        var userProfileMapper = Mappers.getMapper(UserProfileMapper.class);
        var shortUserProfileStrategy = new MapShortUserProfileStrategy(
            userPositionRepository,
            userProjectRepository,
            userStackRepository,
            userProfileMapper
        );
        var fullUserProfileStrategy = new MapFullUserProfileStrategy(
            userPositionRepository,
            userProjectRepository,
            userStackRepository,
            userProfileMapper
        );
        userService = new UserService(
            userRepository,
            new MapUserProfileFactory(
                shortUserProfileStrategy,
                fullUserProfileStrategy
            ),
            Mappers.getMapper(UserAnniversaryMapper.class)
        );
    }

    private User newUser() {
        var userId = RANDOM.nextInt();
        var user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");
        when(user.getEmail()).thenReturn("user@example.com");
        when(user.getAvatar()).thenReturn("https://avatar.com/");
        when(user.getGrade()).thenReturn("Senior");
        when(user.getGender()).thenReturn(Gender.MALE);
        when(user.getPhone()).thenReturn(Map.of(
            "home", "+123456789",
            "mobile", "+987654321"
        ));
        when(user.getBirthDate()).thenReturn(LocalDate.of(1990, Month.APRIL, 21));
        return user;
    }

    private UserProject newUserProject() {
        var projectId = RANDOM.nextInt();
        var project = mock(Project.class);
        when(project.getId()).thenReturn(projectId);
        when(project.getName()).thenReturn("TeamConnect");
        var userProject = mock(UserProject.class);
        when(userProject.getProject()).thenReturn(project);
        when(userProject.getStartDate()).thenReturn(LocalDate.of(2020, Month.SEPTEMBER, 2));
        return userProject;
    }

    private UserPosition newUserPosition() {
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
        return userPosition;
    }

    private UserStack newUserStack() {
        var stackId = RANDOM.nextInt();
        var userStack = mock(UserStack.class);
        var stack = mock(Stack.class);
        when(stack.getId()).thenReturn(stackId);
        when(stack.getName()).thenReturn("Java");
        when(userStack.getStack()).thenReturn(stack);
        return userStack;
    }

    private UserFullProfileDto newFullProfileDto(
        User user,
        UserPosition userPosition,
        UserProject userProject,
        UserStack userStack,
        LocalDate hireDate
    ) {
        return new UserFullProfileDto(
            user.getId(),
            "John",
            "Doe",
            "https://avatar.com/",
            "user@example.com",
            hireDate,
            "Senior",
            Gender.MALE,
            Map.of(
                "home", "+123456789",
                "mobile", "+987654321"
            ),
            List.of(
                new StackDto(userStack.getStack().getId(), "Java")
            ),
            List.of(
                new ProfilePositionDto(
                    userPosition.getPosition().getId(),
                    "Java Developer",
                    new ProfileDepartmentDto(
                        userPosition.getPosition().getDepartment().getId(),
                        "Software Development"
                    )
                )
            ),
            List.of(
                new ProfileProjectDto(
                    userProject.getProject().getId(),
                    "TeamConnect",
                    LocalDate.of(2020, Month.SEPTEMBER, 2)
                )
            ),
            LocalDate.of(1990, Month.APRIL, 21)
        );
    }

    private UserShortProfileDto newShortProfileDto(
        User user,
        UserStack userStack,
        UserPosition userPosition,
        UserProject userProject,
        LocalDate hireDate
    ) {
        return new UserShortProfileDto(
            user.getId(),
            "John",
            "Doe",
            "https://avatar.com/",
            "user@example.com",
            hireDate,
            "Senior",
            Gender.MALE,
            List.of(
                new StackDto(userStack.getStack().getId(), "Java")
            ),
            List.of(
                new ProfilePositionDto(
                    userPosition.getPosition().getId(),
                    "Java Developer",
                    new ProfileDepartmentDto(
                        userPosition.getPosition().getDepartment().getId(),
                        "Software Development"
                    )
                )
            ),
            List.of(
                new ProfileProjectDto(
                    userProject.getProject().getId(),
                    "TeamConnect",
                    LocalDate.of(2020, Month.SEPTEMBER, 2)
                )
            )
        );
    }

    @Test
    void profile_repositoryReturnsEntity_returnsDto() {
        var user = newUser();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        var userProject = newUserProject();
        when(userProjectRepository.findByUserIdAndNow(eq(user.getId()), any())).thenReturn(List.of(userProject));

        var userPosition = newUserPosition();
        when(userPositionRepository.findByUserIdAndNow(eq(user.getId()), any())).thenReturn(List.of(userPosition));

        var userStack = newUserStack();
        when(userStackRepository.findByUserId(user.getId())).thenReturn(List.of(userStack));

        var hireDate = mock(LocalDate.class);
        when(userPositionRepository.findHireDateByUserId(user.getId())).thenReturn(Optional.of(hireDate));

        var result = userService.profile("user@example.com");

        var expected = newFullProfileDto(
            user,
            userPosition,
            userProject,
            userStack,
            hireDate
        );
        assertEquals(expected, result);
    }

    @Test
    void profile_repositoryReturnsEmpty_throwsException() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.profile("user@example.com"));
    }

    @Test
    void getAnniversariesBetween_repositoryReturnsEntity_returnsDto() {
        var userId = RANDOM.nextInt();
        var anniversary = mock(Anniversary.class);
        when(anniversary.getUserId()).thenReturn(userId);
        when(anniversary.getFirstName()).thenReturn("John");
        when(anniversary.getLastName()).thenReturn("Doe");
        when(anniversary.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(anniversary.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(userRepository.findAnniversaries(2, 1, 2, 20))
            .thenReturn(List.of(anniversary));

        var actual = userService.getAnniversariesBetween("01-02", "20-02");

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
    void getAnniversariesBetween_repositoryReturnsEmpty_returnsDto() {
        when(userRepository.findAnniversaries(2, 1, 2, 20))
            .thenReturn(List.of());

        var actual = userService.getAnniversariesBetween("01-02", "20-02");

        assertEquals(List.of(), actual);
    }

    @Test
    void getAnniversariesBetween_singleDate_returnsDto() {
        var userId = RANDOM.nextInt();
        var anniversary = mock(Anniversary.class);
        when(anniversary.getUserId()).thenReturn(userId);
        when(anniversary.getFirstName()).thenReturn("John");
        when(anniversary.getLastName()).thenReturn("Doe");
        when(anniversary.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(anniversary.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(userRepository.findAnniversaries(2, 15, 2, 15))
            .thenReturn(List.of(anniversary));

        var actual = userService.getAnniversariesBetween("15-02", "15-02");

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
    void getAnniversariesBetween_startAfterEnd_returnsDto() {
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

        var actual = userService.getAnniversariesBetween("10-12", "20-02");

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

    @Test
    void getUserById_roleEmployee_returnsShortDto() {
        var user = newUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("EMPLOYEE");

        var userProject = newUserProject();
        when(userProjectRepository.findByUserIdAndNow(eq(user.getId()), any())).thenReturn(List.of(userProject));

        var userPosition = newUserPosition();
        when(userPositionRepository.findByUserIdAndNow(eq(user.getId()), any())).thenReturn(List.of(userPosition));

        var userStack = newUserStack();
        when(userStackRepository.findByUserId(user.getId())).thenReturn(List.of(userStack));

        var hireDate = mock(LocalDate.class);
        when(userPositionRepository.findHireDateByUserId(user.getId())).thenReturn(Optional.of(hireDate));

        var result = userService.getUserById("user@example.com", user.getId());

        var expected = newShortProfileDto(
            user,
            userStack,
            userPosition,
            userProject,
            hireDate
        );
        assertEquals(expected, result);
    }

    @Test
    void getUserById_roleAdmin_returnsFullDto() {
        var user = newUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("ADMIN");

        var userProject = newUserProject();
        when(userProjectRepository.findByUserIdAndNow(eq(user.getId()), any())).thenReturn(List.of(userProject));

        var userPosition = newUserPosition();
        when(userPositionRepository.findByUserIdAndNow(eq(user.getId()), any())).thenReturn(List.of(userPosition));

        var userStack = newUserStack();
        when(userStackRepository.findByUserId(user.getId())).thenReturn(List.of(userStack));

        var hireDate = mock(LocalDate.class);
        when(userPositionRepository.findHireDateByUserId(user.getId())).thenReturn(Optional.of(hireDate));

        var result = userService.getUserById("user@example.com", user.getId());

        var expected = newFullProfileDto(
            user,
            userPosition,
            userProject,
            userStack,
            hireDate
        );
        assertEquals(expected, result);
    }

    @Test
    void getUserById_repositoryReturnsEmpty_throwsException() {
        var userId = RANDOM.nextInt();
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("EMPLOYEE");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("user@example.com", userId));
    }
}
