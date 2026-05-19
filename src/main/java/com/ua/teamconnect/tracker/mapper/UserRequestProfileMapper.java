package com.ua.teamconnect.tracker.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.ua.teamconnect.tracker.model.dto.UserUpdateProfileDto;
import com.ua.teamconnect.tracker.model.entity.User;

@Mapper
public interface UserRequestProfileMapper {

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "firstName", ignore = true)
	@Mapping(target = "lastName", ignore = true)
	@Mapping(target = "birthDate", ignore = true)
	@Mapping(target = "grade", ignore = true)
	@Mapping(target = "gender", ignore = true)
	@Mapping(target = "password", ignore = true)
	void updateEntityFromDto(UserUpdateProfileDto dto, @MappingTarget User entity);

}
