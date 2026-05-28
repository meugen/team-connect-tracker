package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserAnniversaryMapper;
import com.ua.teamconnect.tracker.mapper.UserPositionMapper;
import com.ua.teamconnect.tracker.mapper.UserRequestProfileMapper;
import com.ua.teamconnect.tracker.model.dto.UserAnniversaryDto;
import com.ua.teamconnect.tracker.model.dto.UserProfile;
import com.ua.teamconnect.tracker.model.dto.UserUpdateProfileDto;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.projection.Anniversary;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.specification.user.position.UserPositionSpecificationBuilder;
import com.ua.teamconnect.tracker.service.strategy.userprofile.MapUserProfileFactory;
import com.ua.teamconnect.tracker.service.strategy.userprofile.MapUserProfileStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private static final Random RANDOM = new Random();

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
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
        passwordEncoder = mock(PasswordEncoder.class);
        userPositionSpecificationBuilder = mock(UserPositionSpecificationBuilder.class);
        userPositionRepository = mock(UserPositionRepository.class);
        userService = new UserService(
            userRepository,
            passwordEncoder,
            new MapUserProfileFactory(
                shortUserProfileStrategy,
                fullUserProfileStrategy
            ),
            Mappers.getMapper(UserAnniversaryMapper.class),
            Mappers.getMapper(UserRequestProfileMapper.class),
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
    
    @Test
    void updateProfile_userExists_updatesUserAndSaves() {
        var email = "user@example.com";

        var dto = new UserUpdateProfileDto("https://new-avatar.com",
                        Map.of("work", "+380697554332", "home", "+380441234567"), "new_password");

        var user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_password");

        userService.updateProfile(email, dto);

        verify(passwordEncoder).encode("new_password");
        verify(userRepository).save(user);

        assertEquals("encoded_password", user.getPassword());
    }

    @Test
    void updateProfile_withoutPassword_doesNotEncodePassword() {
        var email = "user@example.com";
        var dto = new UserUpdateProfileDto("https://new-avatar.com", Map.of("work", "+380697554332"), null);
        var user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.updateProfile(email, dto);

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(user);
    }
    
    @Test
    void updateProfile_userNotFound_throwsException() {
        var email = "user@example.com";
        var dto = mock(UserUpdateProfileDto.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(email, dto));

        verify(userRepository, never()).save(any());
    }
}
