package com.ua.teamconnect.tracker.service.strategy.userprofile;

import com.ua.teamconnect.tracker.mapper.UserProfileMapper;
import com.ua.teamconnect.tracker.model.dto.*;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.entity.UserPosition;
import com.ua.teamconnect.tracker.model.entity.UserProject;
import com.ua.teamconnect.tracker.model.entity.UserStack;
import com.ua.teamconnect.tracker.model.pojo.Gender;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserStackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MapFullUserProfileStrategyTest extends AbstractMapUserProfileStrategyTest {

    private UserPositionRepository userPositionRepository;
    private UserProjectRepository userProjectRepository;
    private UserStackRepository userStackRepository;
    private MapFullUserProfileStrategy strategy;

    @BeforeEach
    void setupStrategy() {
        userPositionRepository = mock(UserPositionRepository.class);
        userProjectRepository = mock(UserProjectRepository.class);
        userStackRepository = mock(UserStackRepository.class);
        strategy = new MapFullUserProfileStrategy(
            userPositionRepository,
            userProjectRepository,
            userStackRepository,
            Mappers.getMapper(UserProfileMapper.class)
        );
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

    @Test
    void entityToDto_user_convertedToDto() {
        var user = newUser();
        var userStack = newUserStack();
        var userPosition = newUserPosition();
        var userProject = newUserProject();
        var hireDate = mock(LocalDate.class);
        when(userStackRepository.findByUserId(user.getId())).thenReturn(List.of(userStack));
        when(userPositionRepository.findByUserIdAndNow(eq(user.getId()), any())).thenReturn(List.of(userPosition));
        when(userProjectRepository.findByUserIdAndNow(eq(user.getId()), any())).thenReturn(List.of(userProject));
        when(userPositionRepository.findHireDateByUserId(user.getId())).thenReturn(Optional.of(hireDate));

        var actual = strategy.entityToDto(user);

        var expected = newFullProfileDto(
            user,
            userPosition,
            userProject,
            userStack,
            hireDate
        );
        assertEquals(expected, actual);
    }
}
