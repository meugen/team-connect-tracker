package com.ua.teamconnect.tracker.config.jdbc;

import com.ua.teamconnect.tracker.config.secrets.SecretsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamConnectConnectionDetails implements JdbcConnectionDetails {

    private final Environment environment;
    private final SecretsProvider secretsProvider;

    @Override
    public String getUsername() {
        return environment.getProperty("DB_USERNAME");
    }

    @Override
    public String getPassword() {
        return secretsProvider.dbPassword();
    }

    @Override
    public String getJdbcUrl() {
        return environment.getProperty("DB_URL");
    }
}
