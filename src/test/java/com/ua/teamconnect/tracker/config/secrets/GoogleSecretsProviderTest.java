package com.ua.teamconnect.tracker.config.secrets;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GoogleSecretsProviderTest {

    private GoogleSecretsProvider secretsProvider;

    @BeforeEach
    void setupSecretsProvider() {
        secretsProvider = new GoogleSecretsProvider(
            "test-project",
            "test-db-password-secret"
        );
    }

    @Test
    void dbPassword_from_google() {
        try (MockedStatic<SecretManagerServiceClient> utilities = mockStatic(SecretManagerServiceClient.class)) {
            var client = mock(SecretManagerServiceClient.class);
            utilities.when(SecretManagerServiceClient::create)
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
            assertEquals("test-db-password-secret", versionName.getSecret());
            assertEquals("latest", versionName.getSecretVersion());
        }
    }
}
