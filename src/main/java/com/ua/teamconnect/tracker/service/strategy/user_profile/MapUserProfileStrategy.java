package com.ua.teamconnect.tracker.service.strategy.user_profile;

import com.ua.teamconnect.tracker.model.dto.BasicUserInfo;
import com.ua.teamconnect.tracker.model.entity.User;

public interface MapUserProfileStrategy {

    BasicUserInfo entityToDto(User user);
}
