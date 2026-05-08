package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.PositionDto;
import com.ua.teamconnect.tracker.model.entity.Position;
import org.mapstruct.Mapper;

@Mapper
public interface PositionMapper {

    PositionDto entityToDto(Position position);
}
