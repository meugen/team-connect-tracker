package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.TaskDto;
import com.ua.teamconnect.tracker.model.dto.TaskRequestDto;
import com.ua.teamconnect.tracker.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface TaskMapper {

    void requestDtoToEntity(TaskRequestDto requestDto, @MappingTarget Task entity);
    TaskDto entityToDto(Task entity);
}
