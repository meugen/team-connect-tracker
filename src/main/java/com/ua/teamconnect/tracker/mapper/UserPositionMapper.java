package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.ProfilePositionDto;
import com.ua.teamconnect.tracker.model.entity.UserPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = {DepartmentMapper.class})
public interface UserPositionMapper {

    @Mapping(target = "id", source = "userPosition.position.id")
    @Mapping(target = "name", source = "userPosition.position.name")
    @Mapping(target = "department", source = "userPosition.position.department")
    ProfilePositionDto entityToDto(UserPosition userPosition);
    @SuppressWarnings("unused") // Used in generated mapper for UserProfileMapper
    List<ProfilePositionDto> entityListToDtoList(List<UserPosition> userPositions);
}
