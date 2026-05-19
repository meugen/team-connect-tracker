package com.ua.teamconnect.tracker.service.adapter.storage;

import org.springframework.core.env.Environment;

public class StorageAdapterFactory {

    public static StorageAdapter build(Environment env) {
        var storageAdapter = DropboxStorageAdapter.build(env);
        if (storageAdapter.isPresent()) return storageAdapter.get();
        throw new IllegalStateException("No storage adapter found");
    }
}
