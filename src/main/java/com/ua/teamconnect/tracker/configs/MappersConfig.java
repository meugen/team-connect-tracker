package com.ua.teamconnect.tracker.configs;

import com.ua.teamconnect.tracker.mappers.StackMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfig {

    @Bean
    public StackMapper stackMapper() {
        return Mappers.getMapper(StackMapper.class);
    }
}
