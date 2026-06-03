package com.ua.teamconnect.tracker.config.secrets;

import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

class SecretsProviderFactoryTest {

    private ClassLoader classLoader;
    private SecretsProviderFactory factory;

    @BeforeEach
    void setupFactory() {
        var resourceLoader = mock(ResourceLoader.class);
        this.classLoader = mock(ClassLoader.class);
        when(resourceLoader.getClassLoader()).thenReturn(classLoader);
        this.factory = new SecretsProviderFactory(mock(), resourceLoader);
    }

    @Test
    void getObject_noServiceAccount_environment() {
        when(classLoader.getResourceAsStream("teamconnect-2-2ee0e331a62b.json"))
            .thenReturn(null);

        var result = factory.getObject();

        assertInstanceOf(EnvironmentSecretsProvider.class, result);
    }

    @Test
    void getObject_validServiceAccount_google() {
        var inputStream = mock(InputStream.class);
        when(classLoader.getResourceAsStream("teamconnect-2-2ee0e331a62b.json"))
            .thenReturn(inputStream);

        try (MockedStatic<GoogleCredentials> utilities = mockStatic(GoogleCredentials.class)) {
            utilities.when(() -> GoogleCredentials.fromStream(inputStream))
                .thenReturn(mock(GoogleCredentials.class));

            var result = factory.getObject();

            assertInstanceOf(GoogleSecretsProvider.class, result);
        }
    }
}
