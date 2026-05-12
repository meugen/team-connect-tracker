package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserAnniversaryMapper;
import com.ua.teamconnect.tracker.mapper.UserProfileMapper;
import com.ua.teamconnect.tracker.model.dto.UserAnniversaryDto;
import com.ua.teamconnect.tracker.model.dto.UserProfileDto;
import com.ua.teamconnect.tracker.model.entity.projection.Anniversary;
import com.ua.teamconnect.tracker.model.exception.InvalidMonthDayException;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.model.pojo.ProfileDetails;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.UserStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserStackRepository userStackRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserAnniversaryMapper userAnniversaryMapper;

    public UserProfileDto profile(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException(email)
        );
        var stacks = userStackRepository.findByUserId(user.getId());
        var hireDate = userPositionRepository.findHireDateByUserId(user.getId())
            .orElse(null);
        var now = LocalDate.now();
        var positions = userPositionRepository.findByUserIdAndNow(user.getId(), now);
        var projects = userProjectRepository.findByUserIdAndNow(user.getId(), now);
        var details = new ProfileDetails(stacks, projects, positions, hireDate);
        return userProfileMapper.entityToDto(user, details);
    }

    public List<UserAnniversaryDto> anniversaries(String startDate, String endDate) {
        var stream = Stream.<Anniversary>empty();
        for (var pair : toListOfDatesPair(startDate, endDate)) {
            var newStream = userRepository.findAnniversaries(
                pair.startDate().getMonth().getValue(),
                pair.startDate().getDayOfMonth(),
                pair.endDate().getMonth().getValue(),
                pair.endDate().getDayOfMonth()
             ).stream();
            stream = Stream.concat(stream, newStream);
        }
        return userAnniversaryMapper.projectionListTDtoList(stream.toList());
    }

    private static List<DatesPair> toListOfDatesPair(String startDate, String endDate) {
        var start = toMonthDay(startDate);
        var end = toMonthDay(endDate);
        if (start.isBefore(end)) {
            return List.of(new DatesPair(start, end));
        }
        return List.of(
            new DatesPair(start, MonthDay.of(Month.DECEMBER, 31)),
            new DatesPair(MonthDay.of(Month.JANUARY, 1), end)
        );
    }

    private static MonthDay toMonthDay(String date) {
        try {
            return MonthDay.parse(date, DateTimeFormatter.ofPattern("dd-MM"));
        } catch (Exception e) {
            throw new InvalidMonthDayException(date);
        }
    }

    private record DatesPair(MonthDay startDate, MonthDay endDate) {
    }
}
