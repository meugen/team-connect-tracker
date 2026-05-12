package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserAnniversaryMapper;
import com.ua.teamconnect.tracker.mapper.UserProfileMapper;
import com.ua.teamconnect.tracker.model.dto.UserAnniversaryDto;
import com.ua.teamconnect.tracker.model.dto.UserProfileDto;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.model.pojo.ProfileDetails;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.UserStackRepository;
import com.ua.teamconnect.tracker.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.List;

import static com.ua.teamconnect.tracker.util.DateUtil.toMonthDay;

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
        var stream = toListOfDatesPair(startDate, endDate).stream()
            .flatMap(pair -> userRepository.findAnniversaries(
                    pair.first().getMonthValue(),
                    pair.first().getDayOfMonth(),
                    pair.second().getMonthValue(),
                    pair.second().getDayOfMonth()
                ).stream()
            );
        return userAnniversaryMapper.projectionListTDtoList(stream.toList());
    }

    private static List<Pair<MonthDay, MonthDay>> toListOfDatesPair(String startDate, String endDate) {
        var start = toMonthDay(startDate);
        var end = toMonthDay(endDate);
        if (start.equals(end) || start.isBefore(end)) {
            return List.of(new Pair<>(start, end));
        }
        return List.of(
            new Pair<>(start, MonthDay.of(Month.DECEMBER, 31)),
            new Pair<>(MonthDay.of(Month.JANUARY, 1), end)
        );
    }

}
