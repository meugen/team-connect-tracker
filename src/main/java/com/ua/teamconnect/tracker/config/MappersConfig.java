package com.ua.teamconnect.tracker.config;

import com.ua.teamconnect.tracker.mapper.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfig {

    @Bean
    public DepartmentMapper departmentMapper() {
        return Mappers.getMapper(DepartmentMapper.class);
    }

    @Bean
    public StackMapper stackMapper() {
        return Mappers.getMapper(StackMapper.class);
    }

    @Bean
    public PositionMapper positionMapper() {
        return Mappers.getMapper(PositionMapper.class);
    }

    @Bean
    public UserProfileMapper userProfileMapper() {
        return Mappers.getMapper(UserProfileMapper.class);
    }

    @Bean
    public UserDateMapper userHireDateMapper() {
        return Mappers.getMapper(UserDateMapper.class);
    }

    @Bean
    public UserPositionMapper userPositionMapper() {
        return Mappers.getMapper(UserPositionMapper.class);
    }

    @Bean
    public UserRequestProfileMapper userRequestProfileMapper() {
        return Mappers.getMapper(UserRequestProfileMapper.class);
    }

    @Bean
    public HolidayMapper holidayMapper() {
        return Mappers.getMapper(HolidayMapper.class);
    }
    
    @Bean
    public UserBirthdayMapper userBirthdayMapper() {
        return Mappers.getMapper(UserBirthdayMapper.class);
    }
}
