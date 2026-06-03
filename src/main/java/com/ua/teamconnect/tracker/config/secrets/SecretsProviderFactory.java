package com.ua.teamconnect.tracker.config.secrets;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecretsProviderFactory implements FactoryBean<SecretsProvider> {

    private final Environment environment;
    private final ResourceLoader resourceLoader;

    @Override
    public @Nullable SecretsProvider getObject() {
        var classLoader = resourceLoader.getClassLoader();
        return GoogleSecretsProvider.create(classLoader).orElseGet(
            () -> new EnvironmentSecretsProvider(environment)
        );
    }

    @Override
    public @Nullable Class<?> getObjectType() {
        return SecretsProvider.class;
    }
}
