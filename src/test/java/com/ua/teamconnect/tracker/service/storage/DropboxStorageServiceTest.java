package com.ua.teamconnect.tracker.service.storage;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DropboxStorageServiceTest {

    @Test
    void upload_noException_uploadsByDropboxPath() throws Exception {
        var fixture = fixture();

        when(fixture.file.getOriginalFilename()).thenReturn("filename.pdf");

        var actual = fixture.service.upload("user@example.com", fixture.file);

        assertEquals("/user/filename.pdf", actual);
        verify(fixture.filesRequests).uploadBuilder("/user/filename.pdf");
        verify(fixture.uploadBuilder).withMode(WriteMode.OVERWRITE);
    }

    @Test
    void upload_emailWithCorporateDomain_usesUsernameAsFolderName() throws Exception {
        var fixture = fixture();

        when(fixture.file.getOriginalFilename()).thenReturn("avatar.png");

        fixture.service.upload("john.smith@sombra.com", fixture.file);

        verify(fixture.filesRequests).uploadBuilder("/john.smith/avatar.png");
    }

    @Test
    void upload_emailWithUppercase_trimsAndLowercasesFolderName() throws Exception {
        var fixture = fixture();

        when(fixture.file.getOriginalFilename()).thenReturn("avatar.png");

        fixture.service.upload("  John.Smith@sombra.com  ", fixture.file);

        verify(fixture.filesRequests).uploadBuilder("/john.smith/avatar.png");
    }

    @Test
    void upload_filenameLongerThanLimit_truncatesNameAndKeepsExtension() throws Exception {
        var fixture = fixture();

        var longName = "a".repeat(150) + ".pdf";
        when(fixture.file.getOriginalFilename()).thenReturn(longName);

        fixture.service.upload("user@example.com", fixture.file);

        var expectedFilename = "a".repeat(100) + ".pdf";
        verify(fixture.filesRequests).uploadBuilder("/user/" + expectedFilename);
    }

    @Test
    void upload_filenameWithoutExtension_truncatesFilename() throws Exception {
        var fixture = fixture();

        var longName = "b".repeat(150);
        when(fixture.file.getOriginalFilename()).thenReturn(longName);

        fixture.service.upload("user@example.com", fixture.file);

        var expectedFilename = "b".repeat(100);
        verify(fixture.filesRequests).uploadBuilder("/user/" + expectedFilename);
    }

    @Test
    void upload_filenameWithMultipleDots_truncatesBaseNameAndKeepsLastExtension() throws Exception {
        var fixture = fixture();

        var filename = "very.long." + "c".repeat(150) + ".jpg";
        when(fixture.file.getOriginalFilename()).thenReturn(filename);

        fixture.service.upload("user@example.com", fixture.file);

        var expectedBaseName = ("very.long." + "c".repeat(150)).substring(0, 100);
        verify(fixture.filesRequests).uploadBuilder("/user/" + expectedBaseName + ".jpg");
    }

    @Test
    void upload_filenameWithPathTraversal_usesOnlyFileName() throws Exception {
        var fixture = fixture();

        when(fixture.file.getOriginalFilename()).thenReturn("../../../cat.png");

        fixture.service.upload("user@example.com", fixture.file);

        verify(fixture.filesRequests).uploadBuilder("/user/cat.png");
    }

    @Test
    void upload_filenameWithInvalidCharacters_replacesThem() throws Exception {
        var fixture = fixture();

        when(fixture.file.getOriginalFilename()).thenReturn("my:file*name?.png");

        fixture.service.upload("user@example.com", fixture.file);

        verify(fixture.filesRequests).uploadBuilder("/user/my_file_name_.png");
    }

    @Test
    void upload_emptyFilename_usesDefaultFilename() throws Exception {
        var fixture = fixture();

        when(fixture.file.getOriginalFilename()).thenReturn("");

        fixture.service.upload("user@example.com", fixture.file);

        verify(fixture.filesRequests).uploadBuilder("/user/file");
    }

    @Test
    void upload_nullFilename_usesDefaultFilename() throws Exception {
        var fixture = fixture();

        when(fixture.file.getOriginalFilename()).thenReturn(null);

        fixture.service.upload("user@example.com", fixture.file);

        verify(fixture.filesRequests).uploadBuilder("/user/file");
    }

    @Test
    void upload_exception_refreshesTokenAndRetries() throws Exception {
        var client = mock(DbxClientV2.class);
        var filesRequests = mock(DbxUserFilesRequests.class);
        var uploadBuilder = mock(UploadBuilder.class);

        when(client.files()).thenReturn(filesRequests);
        when(filesRequests.uploadBuilder(anyString())).thenReturn(uploadBuilder);
        when(uploadBuilder.withMode(WriteMode.OVERWRITE)).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(any()))
            .thenThrow(new RuntimeException("Upload failed"));

        var service = new DropboxStorageService(client);

        var file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("filename.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes(UTF_8)));

        assertThrows(
            IllegalStateException.class,
            () -> service.upload("user@example.com", file)
        );

        verify(client).refreshAccessToken();
        verify(filesRequests, times(2)).uploadBuilder("/user/filename.pdf");
    }

    @Test
    void shareLink_noException_returnsLink() throws Exception {
        var client = mock(DbxClientV2.class);
        var sharingRequests = mock(DbxUserSharingRequests.class);
        var linkMetadata = mock(SharedLinkMetadata.class);

        when(client.sharing()).thenReturn(sharingRequests);
        when(sharingRequests.createSharedLinkWithSettings(eq("/user/filename.pdf"), any()))
            .thenReturn(linkMetadata);
        when(linkMetadata.getUrl()).thenReturn("https://www.dropbox.com/s/abc123/filename.pdf?dl=0");

        var service = new DropboxStorageService(client);

        var actual = service.shareLink("/user/filename.pdf");

        assertEquals("https://www.dropbox.com/s/abc123/filename.pdf?dl=0", actual);
    }

    @Test
    void shareLink_exception_refreshesTokenAndRetries() throws Exception {
        var client = mock(DbxClientV2.class);
        var sharingRequests = mock(DbxUserSharingRequests.class);

        when(client.sharing()).thenReturn(sharingRequests);
        when(sharingRequests.createSharedLinkWithSettings(eq("/user/filename.pdf"), any()))
            .thenThrow(new RuntimeException("Share link failed"));

        var service = new DropboxStorageService(client);

        assertThrows(
            IllegalStateException.class,
            () -> service.shareLink("/user/filename.pdf")
        );

        verify(client).refreshAccessToken();
        verify(sharingRequests, times(2)).createSharedLinkWithSettings(eq("/user/filename.pdf"), any());
    }

    private TestFixture fixture() throws Exception {
        var client = mock(DbxClientV2.class);
        var filesRequests = mock(DbxUserFilesRequests.class);
        var uploadBuilder = mock(UploadBuilder.class);
        var fileMetadata = mock(FileMetadata.class);
        var file = mock(MultipartFile.class);

        when(client.files()).thenReturn(filesRequests);
        when(filesRequests.uploadBuilder(anyString())).thenReturn(uploadBuilder);
        when(uploadBuilder.withMode(WriteMode.OVERWRITE)).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(any())).thenReturn(fileMetadata);
        when(fileMetadata.getPathLower()).thenReturn("/user/filename.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes(UTF_8)));

        var service = new DropboxStorageService(client);

        return new TestFixture(
            service,
            client,
            filesRequests,
            uploadBuilder,
            fileMetadata,
            file
        );
    }

    private record TestFixture(
        DropboxStorageService service,
        DbxClientV2 client,
        DbxUserFilesRequests filesRequests,
        UploadBuilder uploadBuilder,
        FileMetadata fileMetadata,
        MultipartFile file
    ) {
    }
}
