package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.pojo.Gender;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

abstract class UserRelatedRepositoryTest {

    User newUser() {
        var user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("user@example.com");
        user.setGender(Gender.MALE);
        user.setPassword("password");
        user.setRole("DEVELOPER");
        user.setBirthDate(LocalDate.of(1990, Month.JANUARY, 1));
        user.setAvatar("https://avatar.com");
        user.setPhone(Map.of(
            "home", "+123456789",
            "mobile", "+987654321"
        ));
        user.setGrade("Senior");
        user.setStatus("ACTIVE");
        return user;
    }
}
