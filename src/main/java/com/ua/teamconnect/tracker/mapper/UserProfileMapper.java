package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.UserFullProfileDto;
import com.ua.teamconnect.tracker.model.dto.UserShortProfileDto;
import com.ua.teamconnect.tracker.model.entity.User;
import com.ua.teamconnect.tracker.model.pojo.ProfileDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {UserPositionMapper.class, UserStackMapper.class, UserProjectMapper.class})
public interface UserProfileMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "avatar", source = "user.avatar")
    @Mapping(target = "workEmail", source = "user.email")
    @Mapping(target = "hireDate", source = "profileDetails.hireDate")
    @Mapping(target = "grade", source = "user.grade")
    @Mapping(target = "gender", source = "user.gender")
    @Mapping(target = "phones", source = "user.phone")
    @Mapping(target = "stacks", source = "profileDetails.stacks")
    @Mapping(target = "positions", source = "profileDetails.positions")
    @Mapping(target = "projects", source = "profileDetails.projects")
    @Mapping(target = "birthDate", source = "user.birthDate")
    UserFullProfileDto entityToFullDto(User user, ProfileDetails profileDetails);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "avatar", source = "user.avatar")
    @Mapping(target = "workEmail", source = "user.email")
    @Mapping(target = "hireDate", source = "profileDetails.hireDate")
    @Mapping(target = "grade", source = "user.grade")
    @Mapping(target = "gender", source = "user.gender")
    @Mapping(target = "stacks", source = "profileDetails.stacks")
    @Mapping(target = "positions", source = "profileDetails.positions")
    @Mapping(target = "projects", source = "profileDetails.projects")
    UserShortProfileDto entityToShortDto(User user, ProfileDetails profileDetails);
}
