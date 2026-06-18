package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.HolidayDto;
import com.ua.teamconnect.tracker.model.dto.HolidayRequestDto;
import com.ua.teamconnect.tracker.model.entity.Holiday;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface HolidayMapper {

    @Mapping(target = "dayOfWeek", source = "date.dayOfWeek")
    HolidayDto entityToDto(Holiday holiday);
    @Mapping(target = "id", ignore = true)
    void dtoToEntity(HolidayRequestDto holidayDto, @MappingTarget Holiday holiday);
}
