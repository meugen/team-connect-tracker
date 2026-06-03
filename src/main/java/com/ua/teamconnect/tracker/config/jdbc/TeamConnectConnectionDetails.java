package com.ua.teamconnect.tracker.config.jdbc;

import com.ua.teamconnect.tracker.config.secrets.SecretsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamConnectConnectionDetails implements JdbcConnectionDetails {

    @Value("${spring.datasource.url}")
    private final String dbUrl;
    @Value("${spring.datasource.username:#{null}}")
    private final String dbUsername;
    private final SecretsProvider secretsProvider;

    @Override
    public String getUsername() {
        return dbUsername;
    }

    @Override
    public String getPassword() {
        return secretsProvider.dbPassword();
    }

    @Override
    public String getJdbcUrl() {
        return dbUrl;
    }
}
