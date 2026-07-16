package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.*;
import com.ua.teamconnect.tracker.model.dto.*;
import com.ua.teamconnect.tracker.model.entity.Department;
import com.ua.teamconnect.tracker.model.entity.MediaFile;
import com.ua.teamconnect.tracker.model.entity.Position;
import com.ua.teamconnect.tracker.model.entity.Project;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.UserProject;
import com.ua.teamconnect.tracker.model.entity.projection.UserDate;
import com.ua.teamconnect.tracker.model.exception.InvalidMonthDayException;
import com.ua.teamconnect.tracker.model.exception.NotFoundException;
import com.ua.teamconnect.tracker.repository.MediaFileRepository;
import com.ua.teamconnect.tracker.repository.ProjectRepository;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.specification.user.position.UserPositionSpecificationBuilder;
import com.ua.teamconnect.tracker.service.storage.DropboxStorageService;
import com.ua.teamconnect.tracker.service.strategy.userprofile.MapUserProfileFactory;
import com.ua.teamconnect.tracker.service.strategy.userprofile.MapUserProfileStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
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
    private UserProjectRepository userProjectRepository;
    private ProjectRepository projectRepository;

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
        userProjectRepository = mock(UserProjectRepository.class);
        projectRepository = mock(ProjectRepository.class);
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
            mapUserBirthday,
            userProjectRepository,
            projectRepository
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

        assertThrows(NotFoundException.class, () -> userService.findProfile("user@example.com"));
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

        assertThrows(NotFoundException.class, () -> userService.findUserById("user@example.com", userId));
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
        assertThrows(NotFoundException.class, () -> userService.updateProfile(email, dto));

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
        
        userService.findByBirthdaysBetween("EMPLOYEE", "20-12", "10-01");
        
        verify(userRepository, times(1)).findUsersWithBirthdaysBetween(12, 20, 12, 31);
        verify(userRepository, times(1)).findUsersWithBirthdaysBetween(1, 1, 1, 10);
    }
    
    @Test
    void findBirthdaysBetween_AdminUsers_returnsFullBirthDate() {
        var user = new User();
        user.setId(1);
        user.setBirthDate(LocalDate.of(1990, 6, 15));

        when(userRepository.findUsersWithBirthdaysBetween(6, 1, 6, 30)).thenReturn(List.of(user));

        var result = userService.findByBirthdaysBetween(
            "ADMIN",
            "01-06",
            "30-06"
        );
        
        assertEquals(1, result.size());
        assertEquals("15-06-1990", result.get(0).birthDate());
    }
    
    @Test
    void findBirthdaysBetween_invalidStartDate_throwsException() {
        var exception = assertThrows(
            InvalidMonthDayException.class,
            () -> userService.findByBirthdaysBetween(
                "EMPLOYEE",
                "invalid",
                "30-06"
            )
        );
        assertEquals("Invalid month day: 'invalid'. Required format: dd-MM", exception.getReason());
    }
    
    @Test
    void findBirthdaysBetween_regularRange_queriesRepositoryOnce() {
        when(userRepository.findUsersWithBirthdaysBetween(6, 1, 6, 30)).thenReturn(List.of());

        userService.findByBirthdaysBetween("EMPLOYEE", "01-06", "30-06");

        verify(userRepository, times(1)).findUsersWithBirthdaysBetween(6, 1, 6, 30);
    }
    
    @Test
    void findBirthdaysBetween_sameStartAndEndDate_queriesRepositoryOnce() {
        when(userRepository.findUsersWithBirthdaysBetween(6, 15, 6, 15)).thenReturn(List.of());

        userService.findByBirthdaysBetween("EMPLOYEE", "15-06", "15-06");

        verify(userRepository, times(1)).findUsersWithBirthdaysBetween(6, 15, 6, 15);
    }
    
    @SuppressWarnings("unchecked")
    void assignProject_validProjects_savesNewAssignments() {
        var userId = 1;
        var user = new User();
        user.setId(userId);
        var project1 = new Project();
        project1.setId(10);
        var project2 = new Project();
        project2.setId(20);

        when(projectRepository.findAllById(Set.of(10, 20)))
            .thenReturn(List.of(project1, project2));
        when(userRepository.findById(userId))
            .thenReturn(Optional.of(user));
        when(userProjectRepository.findProjectIdsByUserId(userId))
            .thenReturn(Set.of());

        userService.assignProject(userId, Set.of(10, 20));

        var captor = ArgumentCaptor.forClass(Iterable.class);
        verify(userProjectRepository).saveAll(captor.capture());
        var assignments = new ArrayList<UserProject>();
        ((Iterable<UserProject>) captor.getValue()).forEach(assignments::add);

        assertEquals(2, assignments.size());
        assertEquals(Set.of(10, 20), assignments.stream()
            .map(it -> it.getProject().getId())
            .collect(Collectors.toSet()));
        assertTrue(assignments.stream()
            .allMatch(it -> it.getUser().equals(user)));
        assertTrue(assignments.stream()
            .allMatch(it -> it.getStartDate().equals(LocalDate.now())));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void assignProject_projectAlreadyAssigned_doesNotSaveDuplicate() {
        var userId = 1;
        var user = new User();
        user.setId(userId);
        var assignedProject = new Project();
        assignedProject.setId(10);
        var newProject = new Project();
        newProject.setId(20);

        when(projectRepository.findAllById(Set.of(10, 20)))
            .thenReturn(List.of(assignedProject, newProject));
        when(userRepository.findById(userId))
            .thenReturn(Optional.of(user));
        when(userProjectRepository.findProjectIdsByUserId(userId))
            .thenReturn(Set.of(10));

        userService.assignProject(userId, Set.of(10, 20));

        var captor = ArgumentCaptor.forClass(Iterable.class);

        verify(userProjectRepository).saveAll(captor.capture());

        var assignments = new ArrayList<UserProject>();
        ((Iterable<UserProject>) captor.getValue()).forEach(assignments::add);

        assertEquals(1, assignments.size());
        assertEquals(20, assignments.get(0).getProject().getId());
    }
    
    @Test
    void assignProject_allProjectsAlreadyAssigned_savesEmptyCollection() {
        var userId = 1;
        var user = new User();
        user.setId(userId);
        var project = new Project();
        project.setId(10);

        when(projectRepository.findAllById(Set.of(10)))
            .thenReturn(List.of(project));
        when(userRepository.findById(userId))
            .thenReturn(Optional.of(user));
        when(userProjectRepository.findProjectIdsByUserId(userId))
            .thenReturn(Set.of(10));

        userService.assignProject(userId, Set.of(10));

        verify(userProjectRepository).saveAll(argThat(assignments ->
            !assignments.iterator().hasNext()
        ));
    }
    
    @Test
    void assignProject_projectDoesNotExist_throwsException() {
        when(projectRepository.findAllById(Set.of(10, 20)))
            .thenReturn(List.of(createProject(10)));

        var exception = assertThrows(
            NotFoundException.class,
            () -> userService.assignProject(1, Set.of(10, 20))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals( "Projects with IDs [20] not found", exception.getReason());
        
        verify(userRepository, never()).findById(any());
        verify(userProjectRepository, never()).findProjectIdsByUserId(any());
        verify(userProjectRepository, never()).saveAll(any());
    }
    
    private Project createProject(Integer id) {
        var project = new Project();
        project.setId(id);
        return project;
    }
    
    @Test
    void assignProject_userDoesNotExist_throwsException() {
        var project = new Project();
        project.setId(10);

        when(projectRepository.findAllById(Set.of(10)))
            .thenReturn(List.of(project));

        when(userRepository.findById(1))
            .thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> userService.assignProject(1, Set.of(10))
        );

        verify(userProjectRepository, never()).findProjectIdsByUserId(any());
        verify(userProjectRepository, never()).saveAll(any());
    }
    
    @Test
    void deleteProjects_assignedProjects_setsEndDateAndSaves() {
        var userId = 1;
        var projectIds = Set.of(10, 20);

        var user = new User();
        user.setId(userId);

        var project1 = createProject(10);
        var project2 = createProject(20);

        var assignment1 = new UserProject();
        assignment1.setProject(project1);
        var assignment2 = new UserProject();
        assignment2.setProject(project2);

        when(projectRepository.findAllById(projectIds))
            .thenReturn(List.of(project1, project2));
        when(userRepository.findById(userId))
            .thenReturn(Optional.of(user));
        when(userProjectRepository.findActiveByUserIdAndProjectIds(
            eq(userId),
            eq(projectIds),
            any(LocalDate.class)
        )).thenReturn(List.of(assignment1, assignment2));

        userService.deleteProjects(userId, projectIds);

        assertEquals(LocalDate.now(), assignment1.getEndDate());
        assertEquals(LocalDate.now(), assignment2.getEndDate());

        verify(userProjectRepository)
            .saveAll(List.of(assignment1, assignment2));
    }
    
    @Test
    void deleteProjects_projectsNotAssigned_savesEmptyCollection() {
        var userId = 1;
        var projectIds = Set.of(10);

        var user = new User();
        user.setId(userId);

        var project = createProject(10);

        when(projectRepository.findAllById(projectIds))
            .thenReturn(List.of(project));

        when(userRepository.findById(userId))
            .thenReturn(Optional.of(user));

        when(userProjectRepository.findActiveByUserIdAndProjectIds(
            eq(userId),
            eq(projectIds),
            any(LocalDate.class)
        )).thenReturn(List.of());

        userService.deleteProjects(userId, projectIds);

        verify(userProjectRepository).saveAll(argThat(assignments ->
            !assignments.iterator().hasNext()
        ));
    }
    
    @Test
    void deleteProjects_projectDoesNotExist_throwsException() {
        var projectIds = Set.of(10, 20);

        when(projectRepository.findAllById(projectIds))
            .thenReturn(List.of(createProject(10)));

        var exception = assertThrows(
            NotFoundException.class,
            () -> userService.deleteProjects(1, projectIds)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(
            "Projects with IDs [20] not found",
            exception.getReason()
        );

        verify(userRepository, never()).findById(any());
        verify(userProjectRepository, never())
            .findActiveByUserIdAndProjectIds(any(), any(), any());
        verify(userProjectRepository, never()).saveAll(any());
    }
    
    @Test
    void deleteProjects_userDoesNotExist_throwsException() {
        var userId = 1;
        var projectIds = Set.of(10);
        var project = createProject(10);

        when(projectRepository.findAllById(projectIds))
            .thenReturn(List.of(project));

        when(userRepository.findById(userId))
            .thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> userService.deleteProjects(userId, projectIds)
        );

        verify(userProjectRepository, never())
            .findActiveByUserIdAndProjectIds(any(), any(), any());
        verify(userProjectRepository, never()).saveAll(any());
    }
}
