package com.ua.teamconnect.tracker.config;

import com.ua.teamconnect.tracker.service.adapter.storage.StorageAdapter;
import com.ua.teamconnect.tracker.service.adapter.storage.StorageAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class StorageConfig {

    @Bean
    public StorageAdapter storageAdapter(Environment env) {
        return StorageAdapterFactory.build(env);
    }
}
