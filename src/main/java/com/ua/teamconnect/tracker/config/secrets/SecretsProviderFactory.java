package com.ua.teamconnect.tracker.config.secrets;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecretsProviderFactory implements FactoryBean<SecretsProvider> {

    private final Environment environment;

    @Override
    public @Nullable SecretsProvider getObject() {
        return GoogleSecretsProvider.create().orElseGet(
            () -> new EnvironmentSecretsProvider(environment)
        );
    }

    @Override
    public @Nullable Class<?> getObjectType() {
        return SecretsProvider.class;
    }
}
