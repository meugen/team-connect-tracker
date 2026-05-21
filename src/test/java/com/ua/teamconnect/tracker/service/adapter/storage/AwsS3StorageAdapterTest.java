package com.ua.teamconnect.tracker.service.adapter.storage;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AwsS3StorageAdapterTest {

    @Test
    void upload_noException_fileName() throws IOException {
        var client = mock(S3Client.class);
        var adapter = new AwsS3StorageAdapter(client, "team-connect-2", "eu-central-1");

        var multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("filename.pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getInputStream()).thenReturn(mock());
        var actual = adapter.upload("user@example.com", multipartFile);

        verify(client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertEquals("user_at_example.com/filename.pdf", actual);
    }

    @Test
    void upload_exception_throws() {
        var client = mock(S3Client.class);
        var adapter = new AwsS3StorageAdapter(client, "team-connect-2", "eu-central-1");

        var multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("filename.pdf");
        when(client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenThrow(new RuntimeException("Upload failed"));
        assertThrows(IllegalStateException.class,
            () -> adapter.upload("user@example.com", multipartFile)
        );
    }

    @Test
    void shareLink_noException_link() {
        var client = mock(S3Client.class);
        var adapter = new AwsS3StorageAdapter(client, "team-connect-2", "eu-central-1");

        var actual = adapter.shareLink("folder/filename.pdf");

        assertEquals("https://team-connect-2.s3.eu-central-1.amazonaws.com/folder/filename.pdf", actual);
    }
}
