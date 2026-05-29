package com.ua.teamconnect.tracker.service.strategy.userprofile;

import com.ua.teamconnect.tracker.model.entity.*;
import com.ua.teamconnect.tracker.model.pojo.Gender;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class AbstractMapUserProfileStrategyTest {

    private static final Random RANDOM = new Random();

    User newUser() {
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

    UserProject newUserProject() {
        var projectId = RANDOM.nextInt();
        var project = mock(Project.class);
        when(project.getId()).thenReturn(projectId);
        when(project.getName()).thenReturn("TeamConnect");
        var userProject = mock(UserProject.class);
        when(userProject.getProject()).thenReturn(project);
        when(userProject.getStartDate()).thenReturn(LocalDate.of(2020, Month.SEPTEMBER, 2));
        return userProject;
    }

    UserPosition newUserPosition() {
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

    UserStack newUserStack() {
        var stackId = RANDOM.nextInt();
        var userStack = mock(UserStack.class);
        var stack = mock(Stack.class);
        when(stack.getId()).thenReturn(stackId);
        when(stack.getName()).thenReturn("Java");
        when(userStack.getStack()).thenReturn(stack);
        return userStack;
    }
}
