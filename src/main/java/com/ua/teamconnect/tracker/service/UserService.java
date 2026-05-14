package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserAnniversaryMapper;
import com.ua.teamconnect.tracker.model.dto.BasicUserInfo;
import com.ua.teamconnect.tracker.model.dto.UserAnniversaryDto;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.service.strategy.user_profile.MapUserProfileFactory;
import com.ua.teamconnect.tracker.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.MonthDay;
import java.util.List;

import static com.ua.teamconnect.tracker.util.DateUtil.toMonthDay;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MapUserProfileFactory mapUserProfileFactory;
    private final UserAnniversaryMapper userAnniversaryMapper;

    public BasicUserInfo profile(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException(email)
        );
        return mapUserProfileFactory.full().entityToDto(user);
    }

    public List<UserAnniversaryDto> getAnniversariesBetween(String startDate, String endDate) {
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

    public BasicUserInfo getUserById(String email, Integer userId) {
        var role = userRepository.findRoleByEmail(email);
        var user = userRepository.findById(userId).orElseThrow(
            () -> new UserNotFoundException(userId)
        );
        return mapUserProfileFactory.byRole(role).entityToDto(user);
    }

}
