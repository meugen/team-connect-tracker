package com.ua.teamconnect.tracker.service.strategy.userprofile;

import com.ua.teamconnect.tracker.model.dto.UserProfile;
import com.ua.teamconnect.tracker.model.entity.User;

public interface MapUserProfileStrategy {

    UserProfile entityToDto(User user);
}
