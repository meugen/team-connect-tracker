package com.ua.teamconnect.tracker.config.secrets;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    private final Cache<String, String> cache = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();

    @Override
    public String dbPassword() {
        return getSecretValue("datasourse-password").orElseThrow(
            () -> new IllegalStateException("Could not load DB password")
        );
    }

    private Optional<String> getSecretValue(String name) {
        var result = cache.getIfPresent(name);
        if (result != null) return Optional.of(result);
        try (SecretManagerServiceClient client = buildClient()) {
            var versionName = SecretVersionName.of(
                credentials.getProjectId(),
                name,
                "latest"
            );
            result = client.accessSecretVersion(versionName)
                .getPayload().getData().toStringUtf8();
            cache.put(name, result);
            return Optional.of(result);
        } catch (Exception e) {
            logger.error("Could not load secret with name: %s".formatted(name), e);
            return Optional.empty();
        }
    }

    private SecretManagerServiceClient buildClient() throws IOException {
        var settings = SecretManagerServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();
        return SecretManagerServiceClient.create(settings);
    }

}
