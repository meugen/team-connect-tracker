package com.ua.teamconnect.tracker.config.secrets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnvironmentSecretsProviderTest {

    private Environment environment;
    private EnvironmentSecretsProvider secretsProvider;

    @BeforeEach
    void setupSecretsProvider() {
        environment = mock();
        secretsProvider = new EnvironmentSecretsProvider(environment);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "masterkey", "password", "bvvnvbasnabsnamn"})
    void dbPassword_from_environment(String dbPassword) {
        when(environment.getProperty("DB_PASSWORD")).thenReturn(dbPassword);

        var actual = secretsProvider.dbPassword();

        assertEquals(dbPassword, actual);
    }
}
