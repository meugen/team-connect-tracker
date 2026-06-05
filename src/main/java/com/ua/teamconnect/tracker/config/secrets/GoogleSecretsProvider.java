package com.ua.teamconnect.tracker.config.secrets;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "team.connect.secrets.provider", havingValue = "google")
@RequiredArgsConstructor
class GoogleSecretsProvider implements SecretsProvider {

    @Value("${team.connect.gcloud.project-name}")
    private final String projectName;
    @Value("${team.connect.gcloud.db-password-secret}")
    private final String dbPasswordSecret;
    private final Cache<String, String> cache = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();

    @Override
    public String dbPassword() {
        var result = cache.getIfPresent(dbPasswordSecret);
        if (result != null) return result;
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            var versionName = SecretVersionName.of(
                projectName,
                dbPasswordSecret,
                "latest"
            );
            result = client.accessSecretVersion(versionName)
                .getPayload().getData().toStringUtf8();
            cache.put(dbPasswordSecret, result);
            return result;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
