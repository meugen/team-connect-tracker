package com.ua.teamconnect.tracker.config.jdbc;

import com.ua.teamconnect.tracker.config.secrets.SecretsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamConnectConnectionDetails implements JdbcConnectionDetails {

    private final DataSourceProperties dataSourceProperties;
    private final SecretsProvider secretsProvider;

    @Override
    public String getUsername() {
        return dataSourceProperties.getUsername();
    }

    @Override
    public String getPassword() {
        return secretsProvider.dbPassword();
    }

    @Override
    public String getJdbcUrl() {
        return dataSourceProperties.getUrl();
    }
}
