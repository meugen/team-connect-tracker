package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import com.ua.teamconnect.tracker.service.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilesServiceTest {

    private StorageService storageService;
    private FilesService service;

    @BeforeEach
    void setupService() {
        storageService = mock(StorageService.class);
        service = new FilesService(mock(), storageService);
    }

    @Test
    void uploadFile_valid_sharedLink() {
        when(storageService.upload(eq("user@example.com"), any())).thenReturn("filename.pdf");
        when(storageService.shareLink(eq("filename.pdf"))).thenReturn("https://sharedlink.com/filename.pdf");

        var actual = service.uploadFile("user@example.com", mock());

        var expected = new UploadedFileDto("https://sharedlink.com/filename.pdf");
        assertEquals(expected, actual);
    }
}
