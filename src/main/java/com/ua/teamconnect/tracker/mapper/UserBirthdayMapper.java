package com.ua.teamconnect.tracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.ua.teamconnect.tracker.model.dto.UserBirthdayDto;
import com.ua.teamconnect.tracker.model.entity.User;

@Mapper
public interface UserBirthdayMapper {

    @Mapping(target = "birthDate", source = "formattedBirthDate")
    UserBirthdayDto toDto(User user, String formattedBirthDate);
}
