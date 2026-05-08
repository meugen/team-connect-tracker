package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.ProfileProjectDto;
import com.ua.teamconnect.tracker.model.entity.UserProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface UserProjectMapper {

    @Mapping(target = "id", source = "userProject.project.id")
    @Mapping(target = "name", source = "userProject.project.name")
    ProfileProjectDto entityToDto(UserProject userProject);
    @SuppressWarnings("unused") // Used in generated mapper for UserProfileMapper
    List<ProfileProjectDto> entityListToDtoList(List<UserProject> userProjects);
}
