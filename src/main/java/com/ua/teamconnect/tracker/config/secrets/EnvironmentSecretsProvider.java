package com.ua.teamconnect.tracker.config.secrets;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
class EnvironmentSecretsProvider implements SecretsProvider {

    private final Environment environment;

    @Override
    public String dbPassword() {
        return environment.getProperty("DB_PASSWORD");
    }
}
