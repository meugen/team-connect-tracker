package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserDateMapper;
import com.ua.teamconnect.tracker.mapper.UserPositionMapper;
import com.ua.teamconnect.tracker.mapper.UserRequestProfileMapper;
import com.ua.teamconnect.tracker.model.dto.*;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.repository.MediaFileRepository;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.specification.user.position.UserPositionSpecificationBuilder;
import com.ua.teamconnect.tracker.service.storage.DropboxStorageService;
import com.ua.teamconnect.tracker.service.strategy.userprofile.MapUserProfileFactory;
import com.ua.teamconnect.tracker.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import static com.ua.teamconnect.tracker.util.DateUtil.toMonthDay;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements PageRequestService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapUserProfileFactory mapUserProfileFactory;
    private final UserDateMapper userDateMapper;
    private final UserRequestProfileMapper userRequestProfileMapper;
    private final UserPositionSpecificationBuilder userPositionSpecificationBuilder;
    private final UserPositionRepository userPositionRepository;
    private final UserPositionMapper userPositionMapper;
    private final MediaFileRepository mediaFileRepository;
    private final DropboxStorageService dropboxStorageService;

    public UserProfile findProfile(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(
            () -> new UserNotFoundException(email)
        );
        return mapUserProfileFactory.full().entityToDto(user);
    }

    public List<UserDateDto> findAnniversariesBetween(String startDate, String endDate) {
        var stream = toListOfDatesPair(startDate, endDate).stream()
            .flatMap(pair -> userRepository.findAnniversaries(
                    pair.first().getMonthValue(),
                    pair.first().getDayOfMonth(),
                    pair.second().getMonthValue(),
                    pair.second().getDayOfMonth()
                ).stream()
            );
        return userDateMapper.projectionListTDtoList(stream.toList());
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

    public UserProfile findUserById(String email, Integer userId) {
        var role = userRepository.findRoleByEmail(email);
        var user = userRepository.findById(userId).orElseThrow(
            () -> new UserNotFoundException(userId)
        );
        return mapUserProfileFactory.byRole(role).entityToDto(user);
    }

    @Transactional
    public void updateProfile(String email, UserUpdateProfileDto dto) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        var oldAvatar = user.getAvatar();

        if (dto.avatar() != null && dto.avatar().isPresent()) {
            var newAvatar = dto.avatar().get();
            if (newAvatar != null) {
                mediaFileRepository.findByUrl(newAvatar)
                                .orElseThrow(() -> new IllegalArgumentException("Avatar file not found"));
            }
            user.setAvatar(newAvatar);
            if (!Objects.equals(oldAvatar, newAvatar)) {
                deleteOldAvatar(oldAvatar);
            }
        }
        
        userRequestProfileMapper.updateEntityFromDto(dto, user);
        if (StringUtils.hasText(dto.password())) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        user = userRepository.save(user);
    }
    
    private void deleteOldAvatar(String oldAvatar) {
        if (!StringUtils.hasText(oldAvatar)) {
            return;
        }
        mediaFileRepository.findByUrl(oldAvatar)
            .ifPresent(mediaFile -> {
                mediaFileRepository.delete(mediaFile);
                try {
                    dropboxStorageService.delete(mediaFile.getDropboxPath());
                } catch (Exception e) {
                    log.warn("Failed to delete old avatar from Dropbox: {}", mediaFile.getUrl(), e);
                }
            });
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

    public List<UserDateDto> findNewHires() {
        var endDate = LocalDate.now();
        var startDate = endDate.minusWeeks(1);
        return userRepository.findByHireDate(startDate, endDate).stream()
            .map(userDateMapper::projectionToDto)
            .toList();
    }
}
