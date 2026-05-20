package com.ua.teamconnect.tracker.service.adapter.storage;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StorageAdapterFactoryTest {

    @Test
    void build_dropboxConfigured_dropboxInstance() throws Exception {
        var env = mock(Environment.class);
        when(env.getProperty("DROPBOX_APPNAME")).thenReturn("team-connect-2");
        when(env.getProperty("DROPBOX_REFRESH_TOKEN")).thenReturn("refreshToken");
        when(env.getProperty("DROPBOX_APP_KEY")).thenReturn("appKey");
        when(env.getProperty("DROPBOX_APP_SECRET")).thenReturn("appSecret");
        var factory = new StorageAdapterFactory(false, env);

        var storageAdapter = factory.getObject();

        assertInstanceOf(DropboxStorageAdapter.class, storageAdapter);
    }

    @Test
    void build_noneConfigured_notConfiguredInstance() throws Exception {
        var env = mock(Environment.class);
        var factory = new StorageAdapterFactory(false, env);

        var storageAdapter = factory.getObject();

        assertInstanceOf(NotConfiguredStorageAdapter.class, storageAdapter);
    }

    @Test
    void build_throwIfNotConfigured_thrownException() throws Exception {
        var env = mock(Environment.class);
        var factory = new StorageAdapterFactory(true, env);

        assertThrows(IllegalStateException.class, factory::getObject);
    }
}
