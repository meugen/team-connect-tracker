package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.StackDto;
import com.ua.teamconnect.tracker.model.entity.UserStack;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserStackMapper {

    @Mapping(target = "id", source = "userStack.stack.id")
    @Mapping(target = "name", source = "userStack.stack.name")
    StackDto entityToDto(UserStack userStack);
    @SuppressWarnings("unused") // Used in generated mapper for UserProfileMapper
    List<StackDto> entityListToDtoList(List<UserStack> userStacks);
}
