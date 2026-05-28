package com.ua.teamconnect.tracker.service.strategy.user_profile;

import com.ua.teamconnect.tracker.model.pojo.ProfileDetails;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserStackRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
abstract class AbstractMapUserProfileStrategy {

    private final UserPositionRepository userPositionRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserStackRepository userStackRepository;

    ProfileDetails buildProfileDetails(Integer userId) {
        var stacks = userStackRepository.findByUserId(userId);
        var hireDate = userPositionRepository.findHireDateByUserId(userId)
            .orElse(null);
        var now = LocalDate.now();
        var positions = userPositionRepository.findByUserIdAndNow(userId, now);
        var projects = userProjectRepository.findByUserIdAndNow(userId, now);
        return new ProfileDetails(stacks, projects, positions, hireDate);
    }
}
