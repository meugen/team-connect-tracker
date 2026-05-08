package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.mapper.UserProfileMapper;
import com.ua.teamconnect.tracker.model.dto.UserProfileDto;
import com.ua.teamconnect.tracker.model.exception.UserNotFoundException;
import com.ua.teamconnect.tracker.model.pojo.ProfileDetails;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserRepository;
import com.ua.teamconnect.tracker.repository.UserStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPositionRepository userPositionRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserStackRepository userStackRepository;
    private final UserProfileMapper userProfileMapper;

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
}
