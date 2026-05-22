package com.ua.teamconnect.tracker.service.adapter.storage;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DropboxStorageAdapterTest {

    @Test
    void upload_noException_fileName() throws Exception {
        var client = mock(DbxClientV2.class);
        var filesRequests = mock(DbxUserFilesRequests.class);
        var uploadBuilder = mock(UploadBuilder.class);
        var fileMetadata = mock(FileMetadata.class);
        when(client.files()).thenReturn(filesRequests);
        when(filesRequests.uploadBuilder("/user_at_example.com/filename.pdf")).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(any())).thenReturn(fileMetadata);
        when(fileMetadata.getPathLower()).thenReturn("path_lower.pdf");
        var adapter = new DropboxStorageAdapter(client);

        var multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("filename.pdf");
        var actual = adapter.upload("user@example.com", multipartFile);

        assertEquals("path_lower.pdf", actual);
    }

    @Test
    void upload_exception_throws() throws Exception {
        var client = mock(DbxClientV2.class);
        var filesRequests = mock(DbxUserFilesRequests.class);
        var uploadBuilder = mock(UploadBuilder.class);
        when(client.files()).thenReturn(filesRequests);
        when(filesRequests.uploadBuilder("/user_at_example.com/filename.pdf")).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(any())).thenThrow(new RuntimeException("Upload failed"));
        var adapter = new DropboxStorageAdapter(client);

        var multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("filename.pdf");
        assertThrows(IllegalStateException.class,
            () -> adapter.upload("user@example.com", multipartFile)
        );
    }

    @Test
    void shareLink_noException_link() throws Exception {
        var client = mock(DbxClientV2.class);
        var sharingRequests = mock(DbxUserSharingRequests.class);
        var linkMetadata = mock(SharedLinkMetadata.class);
        when(client.sharing()).thenReturn(sharingRequests);
        when(sharingRequests.createSharedLinkWithSettings(eq("filename.pdf"), any())).thenReturn(linkMetadata);
        when(linkMetadata.getUrl()).thenReturn("https://www.dropbox.com/s/abc123/filename.pdf?dl=0");
        var adapter = new DropboxStorageAdapter(client);

        var actual = adapter.shareLink("filename.pdf");

        assertEquals("https://www.dropbox.com/s/abc123/filename.pdf?dl=0", actual);
    }

    @Test
    void shareLink_exception_throws() throws Exception {
        var client = mock(DbxClientV2.class);
        var sharingRequests = mock(DbxUserSharingRequests.class);
        when(client.sharing()).thenReturn(sharingRequests);
        when(sharingRequests.createSharedLinkWithSettings(eq("filename.pdf"), any()))
            .thenThrow(new RuntimeException("Share link failed"));
        var adapter = new DropboxStorageAdapter(client);

        assertThrows(IllegalStateException.class,
            () -> adapter.shareLink("filename.pdf")
        );
    }
}
