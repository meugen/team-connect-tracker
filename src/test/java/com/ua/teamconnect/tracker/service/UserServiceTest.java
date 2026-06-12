package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserBirthdayMapper;
import com.ua.teamconnect.tracker.mapper.UserDateMapper;
import com.ua.teamconnect.tracker.mapper.UserPositionMapper;
import com.ua.teamconnect.tracker.mapper.UserRequestProfileMapper;
import com.ua.teamconnect.tracker.model.dto.*;
import com.ua.teamconnect.tracker.model.entity.Department;
import com.ua.teamconnect.tracker.model.entity.MediaFile;
import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.projection.UserDate;
import com.ua.teamconnect.tracker.model.exception.InvalidMonthDayException;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.repository.MediaFileRepository;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.specification.user.position.UserPositionSpecificationBuilder;
import com.ua.teamconnect.tracker.service.storage.DropboxStorageService;
import com.ua.teamconnect.tracker.service.storage.userbirthday.MapUserBirthday;
import com.ua.teamconnect.tracker.service.strategy.userprofile.MapUserProfileFactory;
import com.ua.teamconnect.tracker.service.strategy.userprofile.MapUserProfileStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private static final Random RANDOM = new Random();

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MapUserProfileStrategy shortUserProfileStrategy;
    private MapUserProfileStrategy fullUserProfileStrategy;
    private UserService userService;
    private UserPositionSpecificationBuilder userPositionSpecificationBuilder;
    private UserPositionRepository userPositionRepository;
    private MediaFileRepository mediaFileRepository;
    private DropboxStorageService dropboxStorageService;
    private MapUserBirthday mapUserBirthday;

    @BeforeEach
    void setupService() {
        userRepository = mock(UserRepository.class);
        shortUserProfileStrategy = mock(MapUserProfileStrategy.class);
        fullUserProfileStrategy = mock(MapUserProfileStrategy.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userPositionSpecificationBuilder = mock(UserPositionSpecificationBuilder.class);
        userPositionRepository = mock(UserPositionRepository.class);
        mediaFileRepository = mock(MediaFileRepository.class);
        dropboxStorageService = mock(DropboxStorageService.class);
        mapUserBirthday = new MapUserBirthday(Mappers.getMapper(UserBirthdayMapper.class));
        userService = new UserService(
            userRepository,
            passwordEncoder,
            new MapUserProfileFactory(
                shortUserProfileStrategy,
                fullUserProfileStrategy
            ),
            Mappers.getMapper(UserDateMapper.class),
            Mappers.getMapper(UserRequestProfileMapper.class),
            userPositionSpecificationBuilder,
            userPositionRepository,
            Mappers.getMapper(UserPositionMapper.class),
            mediaFileRepository,
            dropboxStorageService,
            mapUserBirthday
        );
    }

    @Test
    void findProfile_repositoryReturnsEntity_returnsDto() {
        var user = mock(User.class);
        var dto = mock(UserProfile.class);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(fullUserProfileStrategy.entityToDto(user)).thenReturn(dto);

        var result = userService.findProfile("user@example.com");

        assertEquals(dto, result);
    }

    @Test
    void findProfile_repositoryReturnsEmpty_throwsException() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findProfile("user@example.com"));
    }

    @Test
    void findAnniversariesBetween_repositoryReturnsEntity_returnsDto() {
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
        var userHireDate = mock(UserDate.class);
        when(userHireDate.getUserId()).thenReturn(userId);
        when(userHireDate.getFirstName()).thenReturn("John");
        when(userHireDate.getLastName()).thenReturn("Doe");
        when(userHireDate.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(userHireDate.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(userHireDate.getPosition()).thenReturn(position);
        when(userRepository.findAnniversaries(2, 1, 2, 20))
            .thenReturn(List.of(userHireDate));

        var actual = userService.findAnniversariesBetween("01-02", "20-02");

        var expected = List.of(new UserDateDto(
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
    void findAnniversariesBetween_repositoryReturnsEmpty_returnsDto() {
        when(userRepository.findAnniversaries(2, 1, 2, 20))
            .thenReturn(List.of());

        var actual = userService.findAnniversariesBetween("01-02", "20-02");

        assertEquals(List.of(), actual);
    }

    @Test
    void findAnniversariesBetween_singleDate_returnsDto() {
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
        var anniversary = mock(UserDate.class);
        when(anniversary.getUserId()).thenReturn(userId);
        when(anniversary.getFirstName()).thenReturn("John");
        when(anniversary.getLastName()).thenReturn("Doe");
        when(anniversary.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(anniversary.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(anniversary.getPosition()).thenReturn(position);
        when(userRepository.findAnniversaries(2, 15, 2, 15))
            .thenReturn(List.of(anniversary));

        var actual = userService.findAnniversariesBetween("15-02", "15-02");

        var expected = List.of(new UserDateDto(
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
    void findAnniversariesBetween_startAfterEnd_returnsDto() {
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
        var johnHireDate = mock(UserDate.class);
        when(johnHireDate.getUserId()).thenReturn(johnUserId);
        when(johnHireDate.getFirstName()).thenReturn("John");
        when(johnHireDate.getLastName()).thenReturn("Doe");
        when(johnHireDate.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(johnHireDate.getHireDate()).thenReturn(LocalDate.of(2020, Month.FEBRUARY, 15));
        when(johnHireDate.getPosition()).thenReturn(position);

        var ellisonUserId = RANDOM.nextInt();
        var ellisonHireDate = mock(UserDate.class);
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

        var actual = userService.findAnniversariesBetween("10-12", "20-02");

        var expected = List.of(
            new UserDateDto(
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
            new UserDateDto(
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
    void findUserById_roleEmployee_returnsShortDto() {
        var user = mock(User.class);
        var dto = mock(UserProfile.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("EMPLOYEE");
        when(shortUserProfileStrategy.entityToDto(user)).thenReturn(dto);

        var result = userService.findUserById("user@example.com", user.getId());

        assertEquals(dto, result);
    }

    @Test
    void findUserById_roleAdmin_returnsFullDto() {
        var user = mock(User.class);
        var dto = mock(UserProfile.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("ADMIN");
        when(fullUserProfileStrategy.entityToDto(user)).thenReturn(dto);

        var result = userService.findUserById("user@example.com", user.getId());

        assertEquals(dto, result);
    }

    @Test
    void findUserById_repositoryReturnsEmpty_throwsException() {
        var userId = RANDOM.nextInt();
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("EMPLOYEE");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById("user@example.com", userId));
    }

    @Test
    void updateProfile_userExists_updatesUserAndSaves() {
        var email = "user@example.com";

        var dto = new UserUpdateProfileDto(JsonNullable.of("https://new-avatar.com"),
                        Map.of("work", "+380697554332", "home", "+380441234567"), "new_password");

        var user = new User();
        user.setEmail(email);
        var mediaFile = new MediaFile();
        mediaFile.setUrl("https://new-avatar.com");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_password");
        when(mediaFileRepository.findByUrl("https://new-avatar.com")).thenReturn(Optional.of(mediaFile));

        userService.updateProfile(email, dto);

        verify(passwordEncoder).encode("new_password");
        verify(userRepository).save(user);
        verify(mediaFileRepository).findByUrl("https://new-avatar.com");
        
        assertEquals("encoded_password", user.getPassword());
    }

    @Test
    void updateProfile_withoutPassword_doesNotEncodePassword() {
        var email = "user@example.com";
        var dto = new UserUpdateProfileDto(JsonNullable.of("https://new-avatar.com"), Map.of("work", "+380697554332"), null);
        var user = new User();
        var mediaFile = new MediaFile();
        mediaFile.setUrl("https://new-avatar.com");
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mediaFileRepository.findByUrl("https://new-avatar.com")).thenReturn(Optional.of(mediaFile));

        userService.updateProfile(email, dto);

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(user);
        verify(mediaFileRepository).findByUrl("https://new-avatar.com");
    }

    @Test
    void updateFindProfile_userNotFound_throwsException() {
        var email = "user@example.com";
        var dto = mock(UserUpdateProfileDto.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(email, dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void findNewHires_repositoryReturnsEmpty_returnsEmptyDtoList() {
        var endDate = LocalDate.now();
        var startDate = endDate.minusWeeks(1);
        when(userRepository.findByHireDate(startDate, endDate)).thenReturn(List.of());

        var result = userService.findNewHires();

        assertTrue(result.isEmpty());
    }

    @Test
    void findNewHires_repositoryReturnsNonEmpty_returnsNonEmptyDtoList() {
        var endDate = LocalDate.now();
        var startDate = endDate.minusWeeks(1);
        var userId = RANDOM.nextInt();

        var departmentId = RANDOM.nextInt();
        var department = new Department();
        department.setId(departmentId);
        department.setName("Department");
        var positionId = RANDOM.nextInt();
        var position = new Position();
        position.setId(positionId);
        position.setName("Position");
        position.setDepartment(department);

        var userHireDate = mock(UserDate.class);
        when(userHireDate.getUserId()).thenReturn(userId);
        when(userHireDate.getFirstName()).thenReturn("John");
        when(userHireDate.getLastName()).thenReturn("Doe");
        when(userHireDate.getAvatarUrl()).thenReturn("https://avatar.com/john_doe.png");
        when(userHireDate.getHireDate()).thenReturn(startDate);
        when(userHireDate.getPosition()).thenReturn(position);
        when(userRepository.findByHireDate(startDate, endDate)).thenReturn(List.of(userHireDate));

        var result = userService.findNewHires();

        var expected = new UserDateDto(
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
            startDate
        );
        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
    }
    
    @Test
    void updateProfile_avatarChanged_deletesOldAvatarFromDropbox() {
        var email = "user@example.com";
        var dto = new UserUpdateProfileDto(
                        JsonNullable.of("https://new-avatar.com"),
            Map.of("work", "+380697554332"),
            null
        );

        var oldAvatar = new MediaFile();
        oldAvatar.setId(1);
        oldAvatar.setUrl("https://old-avatar.com");
        oldAvatar.setDropboxPath("/user/old-avatar.png");

        var newAvatar = new MediaFile();
        newAvatar.setId(2);
        newAvatar.setUrl("https://new-avatar.com");
        newAvatar.setDropboxPath("/user/new-avatar.png");

        var user = new User();
        user.setEmail(email);
        user.setAvatar(oldAvatar.getUrl());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mediaFileRepository.findByUrl("https://new-avatar.com"))
            .thenReturn(Optional.of(newAvatar));
        when(mediaFileRepository.findByUrl("https://old-avatar.com"))
        .thenReturn(Optional.of(oldAvatar));

        userService.updateProfile(email, dto);

        verify(dropboxStorageService).delete("/user/old-avatar.png");
        verify(userRepository).save(user);
        verify(mediaFileRepository).findByUrl("https://new-avatar.com");
        verify(mediaFileRepository).findByUrl("https://old-avatar.com");
        verify(dropboxStorageService).delete(oldAvatar.getDropboxPath());
        verify(mediaFileRepository).delete(oldAvatar);
    }
    
    @Test
    void findBirthdaysBetween_crossYearRange_queriesBothRanges() {
        when(userRepository.findUsersWithBirthdaysBetween(12, 20, 12, 31)).thenReturn(List.of());
        when(userRepository.findUsersWithBirthdaysBetween(1, 1, 1, 10)).thenReturn(List.of());
        when(userRepository.findRoleByEmail("user@example.com")).thenReturn("FINANCE");
        
        userService.findBirthdaysBetween("user@example.com", "12-20", "01-10");
        
        verify(userRepository).findUsersWithBirthdaysBetween(12, 20, 12, 31);
        verify(userRepository).findUsersWithBirthdaysBetween(1, 1, 1, 10);
    }
    
    @Test
    void findBirthdaysBetween_duplicateAdminUsers_removesDuplicatesAndReturnsFullBirthDate() {
        var user = new User();
        user.setId(1);
        user.setBirthDate(LocalDate.of(1990, 6, 15));

        when(userRepository.findUsersWithBirthdaysBetween(6, 1, 6, 30)).thenReturn(List.of(user, user));
        when(userRepository.findRoleByEmail(anyString())).thenReturn("ADMIN");

        var result = userService.findBirthdaysBetween(
            "user@example.com",
            "06-01",
            "06-30"
        );
        
        assertEquals(1, result.size());
        assertEquals("06-15-1990", result.get(0).birthDate());
    }
    
    @Test
    void findBirthdaysBetween_invalidStartDate_throwsException() {
        var exception = assertThrows(
            InvalidMonthDayException.class,
            () -> userService.findBirthdaysBetween(
                "user@example.com",
                "invalid",
                "06-30"
            )
        );
        assertEquals("Invalid month day: 'invalid'. Required format: MM-dd", exception.getReason());
    }
}
