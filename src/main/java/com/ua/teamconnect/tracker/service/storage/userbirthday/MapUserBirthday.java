package com.ua.teamconnect.tracker.service.storage.userbirthday;

import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;
import com.ua.teamconnect.tracker.mapper.UserBirthdayMapper;
import com.ua.teamconnect.tracker.model.dto.UserBirthdayDto;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.pojo.UserRole;

@Component
public class MapUserBirthday {

    private static final DateTimeFormatter FULL_DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd-MM");

    private final UserBirthdayMapper mapper;

    public MapUserBirthday(UserBirthdayMapper mapper) {
        this.mapper = mapper;
    }

    public List<UserBirthdayDto> toDto(List<User> users, String role) {
        var formatter = UserRole.canSeeFullBirthDate(role) ? FULL_DATE : SHORT_DATE;
        return users.stream()
                .map(user -> mapper.toDto(user, user.getBirthDate().format(formatter)))
                .toList();
    }
}
