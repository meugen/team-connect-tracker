package com.ua.teamconnect.tracker.service.strategy.userprofile;

import com.ua.teamconnect.tracker.mapper.UserProfileMapper;
import com.ua.teamconnect.tracker.model.dto.UserProfile;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.repository.UserPositionRepository;
import com.ua.teamconnect.tracker.repository.UserProjectRepository;
import com.ua.teamconnect.tracker.repository.UserStackRepository;
import org.springframework.stereotype.Component;

@Component("full")
public class MapFullUserProfileStrategy extends AbstractMapUserProfileStrategy implements MapUserProfileStrategy {

    private final UserProfileMapper mapper;

    public MapFullUserProfileStrategy(
        UserPositionRepository userPositionRepository,
        UserProjectRepository userProjectRepository,
        UserStackRepository userStackRepository,
        UserProfileMapper mapper
    ) {
        super(userPositionRepository, userProjectRepository, userStackRepository);
        this.mapper = mapper;
    }

    @Override
    public UserProfile entityToDto(User user) {
        return mapper.entityToFullDto(user, buildProfileDetails(user.getId()));
    }
}
