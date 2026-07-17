package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.TimeLogDto;
import com.ua.teamconnect.tracker.model.entity.TimeLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = { TaskMapper.class, ProjectMapper.class })
public interface TimeLogMapper {

    @Mapping(target = "mediaFileUrl", source = "timeLog.mediaFile.url")
    @Mapping(target = "task", source = "timeLog.userProjectTask.task")
    @Mapping(target = "project", source = "timeLog.userProjectTask.project")
    TimeLogDto entityToDto(TimeLog timeLog);
    List<TimeLogDto> entityListToDtoList(List<TimeLog> timeLogs);
}
