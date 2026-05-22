package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import com.ua.teamconnect.tracker.service.adapter.storage.StorageAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilesServiceTest {

    private StorageAdapter adapter;
    private FilesService service;

    @BeforeEach
    void setupService() {
        adapter = mock(StorageAdapter.class);
        service = new FilesService(mock(), adapter);
    }

    @Test
    void uploadFile_valid_sharedLink() {
        when(adapter.upload(eq("user@example.com"), any())).thenReturn("filename.pdf");
        when(adapter.shareLink(eq("filename.pdf"))).thenReturn("https://sharedlink.com/filename.pdf");

        var actual = service.uploadFile("user@example.com", mock());

        var expected = new UploadedFileDto("https://sharedlink.com/filename.pdf");
        assertEquals(expected, actual);
    }
}
