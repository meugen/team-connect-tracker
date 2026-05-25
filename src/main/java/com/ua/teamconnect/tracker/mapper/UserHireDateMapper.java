package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.UserHireDateDto;
import com.ua.teamconnect.tracker.model.entity.projection.UserHireDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {UserPositionMapper.class})
public interface UserHireDateMapper {

    @Mapping(target = "id", source = "userId")
    UserHireDateDto projectionToDto(UserHireDate anniversary);
    List<UserHireDateDto> projectionListTDtoList(List<UserHireDate> anniversaries);
}
