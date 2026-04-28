package com.ua.teamconnect.tracker.config;

import com.ua.teamconnect.tracker.mapper.DepartmentMapper;
import com.ua.teamconnect.tracker.mapper.PositionMapper;
import com.ua.teamconnect.tracker.mapper.StackMapper;
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
}
