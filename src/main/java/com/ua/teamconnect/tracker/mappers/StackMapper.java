package com.ua.teamconnect.tracker.mappers;

import com.ua.teamconnect.tracker.dto.StackDto;
import com.ua.teamconnect.tracker.model.Stack;
import org.mapstruct.Mapper;

@Mapper
public interface StackMapper {
    StackDto entityToDto(Stack entity);
}
