package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import com.ua.teamconnect.tracker.model.entity.MediaFile;
import com.ua.teamconnect.tracker.repository.MediaFileRepository;
import com.ua.teamconnect.tracker.service.component.MultipartFileValidator;
import com.ua.teamconnect.tracker.service.storage.DropboxStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MediaFileServiceTest {

    private DropboxStorageService dropboxStorageService;
    private MediaFileService service;
    private MultipartFileValidator validator;
    private MediaFileRepository mediaFileRepository;

    @BeforeEach
    void setupService() {
        validator = mock(MultipartFileValidator.class);
        dropboxStorageService = mock(DropboxStorageService.class);
        mediaFileRepository = mock(MediaFileRepository.class);
        service = new MediaFileService(validator, dropboxStorageService, mediaFileRepository);
    }

    @Test
    void uploadFile_valid_sharedLink() {
        var file = mock(MultipartFile.class);
        
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(1024L);
        when(dropboxStorageService.upload(eq("user@example.com"), eq(file)))
            .thenReturn("/user/filename.pdf");
        when(dropboxStorageService.shareLink("/user/filename.pdf"))
            .thenReturn("https://sharedlink.com/filename.pdf");
        when(mediaFileRepository.save(any(MediaFile.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var actual = service.uploadFile("user@example.com", file);
        var expected = new UploadedFileDto("https://sharedlink.com/filename.pdf");

        assertEquals(expected, actual);

        verify(validator).validate(file);
        verify(dropboxStorageService).upload("user@example.com", file);
        verify(dropboxStorageService).shareLink("/user/filename.pdf");
        verify(mediaFileRepository).save(any(MediaFile.class));
    }
}
