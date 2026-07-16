package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.ProjectDto;
import com.ua.teamconnect.tracker.model.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {

    ProjectDto entityToDto(Project project);
    List<ProjectDto> entityIterableToDtoList(Iterable<Project> projectList);
}
