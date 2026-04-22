package com.ua.teamconnect.tracker.mappers;

import com.ua.teamconnect.tracker.dto.DepartmentDto;
import com.ua.teamconnect.tracker.entities.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DepartmentMapper {

    @Mapping(target = "headId", constant = "0L")
    DepartmentDto entityToDto(Department department);
}
