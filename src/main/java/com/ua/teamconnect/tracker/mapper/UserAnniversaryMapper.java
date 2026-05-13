package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.UserAnniversaryDto;
import com.ua.teamconnect.tracker.model.entity.projection.Anniversary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserAnniversaryMapper {

    @Mapping(target = "id", source = "userId")
    UserAnniversaryDto projectionToDto(Anniversary anniversary);
    List<UserAnniversaryDto> projectionListTDtoList(List<Anniversary> anniversaries);
}
