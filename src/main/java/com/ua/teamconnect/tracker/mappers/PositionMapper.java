package com.ua.teamconnect.tracker.mappers;

import com.ua.teamconnect.tracker.dto.PositionDto;
import com.ua.teamconnect.tracker.model.Position;
import org.mapstruct.Mapper;

@Mapper
public interface PositionMapper {

    PositionDto entityToDto(Position position);
}
