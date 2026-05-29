package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.UserDateDto;
import com.ua.teamconnect.tracker.model.entity.projection.UserDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserDateMapper {

    @Mapping(target = "id", source = "userId")
    UserDateDto projectionToDto(UserDate anniversary);
    List<UserDateDto> projectionListTDtoList(List<UserDate> anniversaries);
}
