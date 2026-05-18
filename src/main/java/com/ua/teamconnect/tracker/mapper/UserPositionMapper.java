package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.PageDto;
import com.ua.teamconnect.tracker.model.dto.ProfilePositionDto;
import com.ua.teamconnect.tracker.model.dto.UserDto;
import com.ua.teamconnect.tracker.model.entity.UserPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(uses = {DepartmentMapper.class})
public interface UserPositionMapper {

    @Mapping(target = "id", source = "userPosition.position.id")
    @Mapping(target = "name", source = "userPosition.position.name")
    @Mapping(target = "department", source = "userPosition.position.department")
    ProfilePositionDto entityToDto(UserPosition userPosition);
    @SuppressWarnings("unused") // Used in generated mapper for UserProfileMapper
    List<ProfilePositionDto> entityListToDtoList(List<UserPosition> userPositions);

    @Mapping(target = "id", source = "userPosition.id.userId")
    @Mapping(target = "firstName", source = "userPosition.user.firstName")
    @Mapping(target = "lastName", source = "userPosition.user.lastName")
    @Mapping(target = "avatarUrl", source = "userPosition.user.avatar")
    @Mapping(target = "position", source = "userPosition")
    UserDto entityToUserDto(UserPosition userPosition);

    @Mapping(
        target = "items", source = "page.content",
        conditionExpression = "java(true)"
    )
    @Mapping(target = "totalPages", source = "page.totalPages")
    @Mapping(target = "totalItems", source = "page.totalElements")
    @Mapping(target = "currentPage", expression = "java(page.getNumber() + 1)")
    PageDto<UserDto> pageToPageUserDto(Page<UserPosition> page);
}
