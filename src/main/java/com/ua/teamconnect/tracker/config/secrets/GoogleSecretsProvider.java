package com.ua.teamconnect.tracker.config.secrets;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
class GoogleSecretsProvider implements SecretsProvider {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSecretsProvider.class);

    static Optional<SecretsProvider> create(ClassLoader classLoader) {
        try {
            var resource = new ClassPathResource("teamconnect-2-2ee0e331a62b.json", classLoader);
            var credentials = GoogleCredentials.fromStream(resource.getInputStream());
            return Optional.of(new GoogleSecretsProvider(credentials));
        } catch (Exception e) {
            logger.error("Could not load teamconnect-2-2ee0e331a62b.json", e);
            return Optional.empty();
        }
    }

    private final GoogleCredentials credentials;

    @Override
    public String dbPassword() {
        try (SecretManagerServiceClient client = buildClient()) {
            var versionName = SecretVersionName.of(
                credentials.getProjectId(),
                "datasourse-password",
                "latest"
            );
            return client.accessSecretVersion(versionName)
                .getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Failed to access secret version", e);
        }
    }

    private SecretManagerServiceClient buildClient() throws IOException {
        var settings = SecretManagerServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();
        return SecretManagerServiceClient.create(settings);
    }

}
