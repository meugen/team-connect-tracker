package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.TimeLogMapper;
import com.ua.teamconnect.tracker.model.dto.TimeLogDto;
import com.ua.teamconnect.tracker.model.exception.NotFoundException;
import com.ua.teamconnect.tracker.repository.TimeLogRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeLogService {

    private final UserRepository userRepository;
    private final TimeLogRepository timeLogRepository;
    private final TimeLogMapper timeLogMapper;

    public List<TimeLogDto> findForUserId(Integer userId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        var timeLogs = timeLogRepository.findAllInPeriod(userId, startDate, endDate);
        return timeLogMapper.entityListToDtoList(timeLogs);
    }

    public List<TimeLogDto> findForUserEmail(String email, LocalDate startDate, LocalDate endDate) {
        return userRepository.findByEmail(email)
            .map(user -> findForUserId(user.getId(), startDate, endDate))
            .orElseThrow(
                () -> NotFoundException.userByEmail(email)
            );
    }
}
