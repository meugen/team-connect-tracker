package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.DepartmentDto;
import com.ua.teamconnect.tracker.model.dto.ProfileDepartmentDto;
import com.ua.teamconnect.tracker.model.entity.Department;
import org.mapstruct.Mapper;

@Mapper
public interface DepartmentMapper {

    DepartmentDto entityToDto(Department department);
    @SuppressWarnings("unused") // Used in generated mapping for UserPositionMapper
    ProfileDepartmentDto entityToProfileDto(Department department);
}
