package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserHireDateMapper;
import com.ua.teamconnect.tracker.mapper.UserPositionMapper;
import com.ua.teamconnect.tracker.model.dto.ProfileDepartmentDto;
import com.ua.teamconnect.tracker.model.dto.ProfilePositionDto;
import com.ua.teamconnect.tracker.model.dto.UserHireDateDto;
import com.ua.teamconnect.tracker.model.dto.UserProfile;
import com.ua.teamconnect.tracker.model.entity.Department;
import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.projection.UserHireDate;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.service.specification.user.position.UserPositionSpecificationBuilder;
import com.ua.teamconnect.tracker.service.strategy.user_profile.MapUserProfileFactory;
import com.ua.teamconnect.tracker.service.strategy.user_profile.MapUserProfileStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private static final Random RANDOM = new Random();

    private UserRepository userRepository;
    private MapUserProfileStrategy shortUserProfileStrategy;
    private MapUserProfileStrategy fullUserProfileStrategy;
    private UserService userService;
    private UserPositionSpecificationBuilder userPositionSpecificationBuilder;
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void setupService() {
        userRepository = mock(UserRepository.class);
        shortUserProfileStrategy = mock(MapUserProfileStrategy.class);
        fullUserProfileStrategy = mock(MapUserProfileStrategy.class);
        userPositionSpecificationBuilder = mock(UserPositionSpecificationBuilder.class);
        userPositionRepository = mock(UserPositionRepository.class);
        userService = new UserService(
            userRepository,
            new MapUserProfileFactory(
                shortUserProfileStrategy,
                fullUserProfileStrategy
            ),
            Mappers.getMapper(UserHireDateMapper.class),
            userPositionSpecificationBuilder,
            userPositionRepository,
            Mappers.getMapper(UserPositionMapper.class)
        );
    }

    @Test
    void profile_repositoryReturnsEntity_returnsDto() {
        var user = mock(User.class);
        var dto = mock(UserProfile.class);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(fullUserProfileStrategy.entityToDto(user)).thenReturn(dto);

        var result = userService.profile("user@example.com");

        assertEquals(dto, result);
    }

    @Test
    void profile_repositoryReturnsEmpty_throwsException() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.profile("user@example.com"));
    }

    @Test
    void getAnniversariesBetween_repositoryReturnsEntity_returnsDto() {
        var departmentId = RANDOM.nextInt();
        var department = new Department();
        department.setId(departmentId);
        department.setName("Department");

        var positionId = RANDOM.nextInt();
        var position = new Position();
        position.setId(positionId);
        position.setName("Position");
        position.setDepartment(department);

        var userId = RANDOM.nextInt();
        var userHireDate = mock(UserHireDate.class);
        when(userHireDate.getUserId()).thenReturn(userId);
        when(userHireDate.getFirstName()).thenReturn("John");
        when(userHireDate.getLastName()).thenReturn("Doe");
        when(userHireDate.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(userHireDate.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(userHireDate.getPosition()).thenReturn(position);
        when(userRepository.findAnniversaries(2, 1, 2, 20))
            .thenReturn(List.of(userHireDate));

        var actual = userService.getAnniversariesBetween("01-02", "20-02");

        var expected = List.of(new UserHireDateDto(
            userId,
            "John",
            "Doe",
            "https://avatar.com/john_doe.png",
            new ProfilePositionDto(
                positionId,
                "Position",
                new ProfileDepartmentDto(
                    departmentId,
                    "Department"
                )
            ),
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
        var departmentId = RANDOM.nextInt();
        var department = new Department();
        department.setId(departmentId);
        department.setName("Department");

        var positionId = RANDOM.nextInt();
        var position = new Position();
        position.setId(positionId);
        position.setName("Position");
        position.setDepartment(department);

        var userId = RANDOM.nextInt();
        var anniversary = mock(UserHireDate.class);
        when(anniversary.getUserId()).thenReturn(userId);
        when(anniversary.getFirstName()).thenReturn("John");
        when(anniversary.getLastName()).thenReturn("Doe");
        when(anniversary.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(anniversary.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(anniversary.getPosition()).thenReturn(position);
        when(userRepository.findAnniversaries(2, 15, 2, 15))
            .thenReturn(List.of(anniversary));

        var actual = userService.getAnniversariesBetween("15-02", "15-02");

        var expected = List.of(new UserHireDateDto(
            userId,
            "John",
            "Doe",
            "https://avatar.com/john_doe.png",
            new ProfilePositionDto(
                positionId,
                "Position",
                new ProfileDepartmentDto(
                    departmentId,
                    "Department"
                )
            ),
            LocalDate.of(2020, Month.FEBRUARY, 15)
        ));
        assertEquals(expected, actual);
    }

    @Test
    void getAnniversariesBetween_startAfterEnd_returnsDto() {
        var departmentId = RANDOM.nextInt();
        var department = new Department();
        department.setId(departmentId);
        department.setName("Department");

        var positionId = RANDOM.nextInt();
        var position = new Position();
        position.setId(positionId);
        position.setName("Position");
        position.setDepartment(department);

        var johnUserId = RANDOM.nextInt();
        var johnHireDate = mock(UserHireDate.class);
        when(johnHireDate.getUserId()).thenReturn(johnUserId);
        when(johnHireDate.getFirstName()).thenReturn("John");
        when(johnHireDate.getLastName()).thenReturn("Doe");
        when(johnHireDate.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(johnHireDate.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(johnHireDate.getPosition()).thenReturn(position);

        var ellisonUserId = RANDOM.nextInt();
        var ellisonHireDate = mock(UserHireDate.class);
        when(ellisonHireDate.getUserId()).thenReturn(ellisonUserId);
        when(ellisonHireDate.getFirstName()).thenReturn("Ellison");
        when(ellisonHireDate.getLastName()).thenReturn("Smith");
        when(ellisonHireDate.getAvatarUrl()).thenReturn("https://avatar.com/ellison_smith.png");
        when(ellisonHireDate.getHireDate()).thenReturn(LocalDate.of(2020, Month.DECEMBER, 20));
        when(ellisonHireDate.getPosition()).thenReturn(position);

        when(userRepository.findAnniversaries(1, 1, 2, 20))
            .thenReturn(List.of(johnHireDate));
        when(userRepository.findAnniversaries(12, 10, 12, 31))
            .thenReturn(List.of(ellisonHireDate));

        var actual = userService.getAnniversariesBetween("10-12", "20-02");

        var expected = List.of(
            new UserHireDateDto(
                ellisonUserId,
                "Ellison",
                "Smith",
                "https://avatar.com/ellison_smith.png",
                new ProfilePositionDto(
                    positionId,
                    "Position",
                    new ProfileDepartmentDto(
                        departmentId,
                        "Department"
                    )
                ),
                LocalDate.of(2020, Month.DECEMBER, 20)
            ),
            new UserHireDateDto(
                johnUserId,
                "John",
                "Doe",
                "https://avatar.com/john_doe.png",
                new ProfilePositionDto(
                    positionId,
                    "Position",
                    new ProfileDepartmentDto(
                        departmentId,
                        "Department"
                    )
                ),
                LocalDate.of(2020, Month.FEBRUARY, 15)
            )
        );
        assertEquals(expected, actual);
    }

    @Test
    void getUserById_roleEmployee_returnsShortDto() {
        var user = mock(User.class);
        var dto = mock(UserProfile.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("EMPLOYEE");
        when(shortUserProfileStrategy.entityToDto(user)).thenReturn(dto);

        var result = userService.getUserById("user@example.com", user.getId());

        assertEquals(dto, result);
    }

    @Test
    void getUserById_roleAdmin_returnsFullDto() {
        var user = mock(User.class);
        var dto = mock(UserProfile.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("ADMIN");
        when(fullUserProfileStrategy.entityToDto(user)).thenReturn(dto);

        var result = userService.getUserById("user@example.com", user.getId());

        assertEquals(dto, result);
    }

    @Test
    void getUserById_repositoryReturnsEmpty_throwsException() {
        var userId = RANDOM.nextInt();
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("EMPLOYEE");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("user@example.com", userId));
    }
}
