package com.ua.teamconnect.tracker.service.adapter.storage;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageAdapterFactory implements FactoryBean<StorageAdapter> {

    @Value("${team.connect.storage.adapter.throw-if-not-configured}")
    private final boolean throwIfNotConfigured;
    private final Environment environment;

    @Override
    public @Nullable StorageAdapter getObject() throws Exception {
        var storageAdapter = DropboxStorageAdapter.build(environment);
        if (storageAdapter.isPresent()) return storageAdapter.get();
        if (throwIfNotConfigured) throw new IllegalStateException("Storage adapter is not configured");
        return new NotConfiguredStorageAdapter();
    }

    @Override
    public @Nullable Class<?> getObjectType() {
        return StorageAdapter.class;
    }
}
