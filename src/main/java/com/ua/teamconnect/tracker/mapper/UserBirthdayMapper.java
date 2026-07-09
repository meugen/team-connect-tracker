package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.UserBirthdayDto;
import com.ua.teamconnect.tracker.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserBirthdayMapper {

    @Mapping(target = "birthDate", source = "formattedBirthDate")
    UserBirthdayDto toDto(User user, String formattedBirthDate);
}
