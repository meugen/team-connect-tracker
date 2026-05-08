package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.StackDto;
import com.ua.teamconnect.tracker.model.entity.Stack;
import org.mapstruct.Mapper;

@Mapper
public interface StackMapper {

    StackDto entityToDto(Stack entity);
}
