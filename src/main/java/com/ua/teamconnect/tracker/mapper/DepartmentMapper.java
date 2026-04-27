package com.ua.teamconnect.tracker.mapper;

import com.ua.teamconnect.tracker.model.dto.DepartmentDto;
import com.ua.teamconnect.tracker.model.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DepartmentMapper {

    @Mapping(target = "headId", constant = "0L")
    DepartmentDto entityToDto(Department department);
}
