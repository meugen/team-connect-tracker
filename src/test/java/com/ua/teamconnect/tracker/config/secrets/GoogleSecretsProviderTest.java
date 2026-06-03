package com.ua.teamconnect.tracker.config.secrets;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.*;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GoogleSecretsProviderTest {

    private GoogleCredentials credentials;
    private GoogleSecretsProvider secretsProvider;

    @BeforeEach
    void setupSecretsProvider() {
        credentials = mock();
        secretsProvider = new GoogleSecretsProvider(credentials);
    }

    @Test
    void dbPassword_from_google() {
        when(credentials.getProjectId()).thenReturn("test-project");

        try (MockedStatic<SecretManagerServiceClient> utilities = mockStatic(SecretManagerServiceClient.class)) {
            var client = mock(SecretManagerServiceClient.class);
            utilities.when(() -> SecretManagerServiceClient.create(any(SecretManagerServiceSettings.class)))
                .thenReturn(client);

            var response = mock(AccessSecretVersionResponse.class);
            var payload = mock(SecretPayload.class);
            var data = ByteString.copyFromUtf8("test-password");
            when(payload.getData()).thenReturn(data);
            when(response.getPayload()).thenReturn(payload);

            var captor = ArgumentCaptor.forClass(SecretVersionName.class);
            when(client.accessSecretVersion(captor.capture())).thenReturn(response);

            var actual = secretsProvider.dbPassword();

            assertEquals("test-password", actual);
            var versionName = captor.getValue();
            assertEquals("test-project", versionName.getProject());
            assertEquals("datasourse-password", versionName.getSecret());
            assertEquals("latest", versionName.getSecretVersion());
        }
    }
}
