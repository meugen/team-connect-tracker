package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserAnniversaryMapper;
import com.ua.teamconnect.tracker.mapper.UserPositionMapper;
import com.ua.teamconnect.tracker.mapper.UserRequestProfileMapper;
import com.ua.teamconnect.tracker.model.dto.PageDto;
import com.ua.teamconnect.tracker.model.dto.UserAnniversaryDto;
import com.ua.teamconnect.tracker.model.dto.UserDto;
import com.ua.teamconnect.tracker.model.dto.UserProfile;
import com.ua.teamconnect.tracker.model.dto.UserUpdateProfileDto;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.service.specification.user.position.UserPositionSpecificationBuilder;
import com.ua.teamconnect.tracker.service.strategy.user_profile.MapUserProfileFactory;
import com.ua.teamconnect.tracker.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.Month;
import java.time.MonthDay;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static com.ua.teamconnect.tracker.util.DateUtil.toMonthDay;

@Service
@RequiredArgsConstructor
public class UserService implements PageRequestService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapUserProfileFactory mapUserProfileFactory;
    private final UserAnniversaryMapper userAnniversaryMapper;
    private final UserRequestProfileMapper userRequestProfileMapper;
    private final UserPositionSpecificationBuilder userPositionSpecificationBuilder;
    private final UserPositionRepository userPositionRepository;
    private final UserPositionMapper userPositionMapper;

    public UserProfile profile(String email) {
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

    public UserProfile getUserById(String email, Integer userId) {
        var role = userRepository.findRoleByEmail(email);
        var user = userRepository.findById(userId).orElseThrow(
            () -> new UserNotFoundException(userId)
        );
        return mapUserProfileFactory.byRole(role).entityToDto(user);
    }
    
    public void updateProfile(String email, UserUpdateProfileDto dto) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        userRequestProfileMapper.updateEntityFromDto(dto, user);
        if (StringUtils.hasText(dto.password())) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        userRepository.save(user);
    }

    public PageDto<UserDto> findFiltered(Map<String, String> params) {
        var filterParams = extractFilterParams(params);
        var pair = userPositionSpecificationBuilder.build(filterParams);
        var pageRequest = pageRequestOf(params);
        var page = userPositionRepository.findAll(pair.first(), pair.second(), pageRequest);
        return userPositionMapper.pageToPageUserDto(page);
    }
    
    private Map<String, String> extractFilterParams(Map<String, String> params) {
        return params.entrySet().stream()
            .filter(entry -> !Set.of(PARAM_PAGE, PARAM_SIZE, PARAM_SORT, PARAM_ORDER).contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, String> allowedSortProperties() {
        return Map.of(
            "firstName", "user.firstName",
            "lastName", "user.lastName",
            "position", "position.name",
            "department", "position.department.name"
        );
    }

    @Override
    public String defaultSort() {
        return "lastName";
    }
}
