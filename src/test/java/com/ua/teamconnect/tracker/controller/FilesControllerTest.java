package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.service.adapter.storage.StorageAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.BodyInserters;

import static com.ua.teamconnect.tracker.util.TestUtil.buildClient;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilesControllerTest extends AuthorizationControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private StorageAdapter storageAdapter;

    @Test
    void uploadFile_lessThan5Mb_isOk() {
        when(storageAdapter.upload(eq("user@example.com"), any()))
            .thenReturn("filename.pdf");
        when(storageAdapter.shareLink("filename.pdf"))
            .thenReturn("https://sharedlink.com/filename.pdf");
        setupValidToken("user@example.com");

        var builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("files/sample-1mb.pdf"));
        buildClient(port).post()
            .uri("/files")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.url").isEqualTo("https://sharedlink.com/filename.pdf");
    }

    @Test
    void uploadFile_moreThan5Mb_isPayloadTooLarge() {
        when(storageAdapter.upload(eq("user@example.com"), any()))
            .thenReturn("filename.pdf");
        when(storageAdapter.shareLink("filename.pdf"))
            .thenReturn("https://sharedlink.com/filename.pdf");
        setupValidToken("user@example.com");

        var builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("files/sample-6mb.pdf"));
        var spec = buildClient(port).post()
            .uri("/files")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange();
        validatePayloadTooLarge(spec);
    }

    @Test
    void uploadFile_wrongExtension_isBadRequest() {
        when(storageAdapter.upload(eq("user@example.com"), any()))
            .thenReturn("filename.pdf");
        when(storageAdapter.shareLink("filename.pdf"))
            .thenReturn("https://sharedlink.com/filename.pdf");
        setupValidToken("user@example.com");

        var builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("files/sample.txt"));
        var spec = buildClient(port).post()
            .uri("/files")
            .header("Authorization", "Bearer " + VALID_TOKEN)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange();
        validateBadRequest(spec);
    }

    @Test
    void uploadFile_invalidToken_isUnauthorized() {
        when(storageAdapter.upload(eq("user@example.com"), any()))
            .thenReturn("filename.pdf");
        when(storageAdapter.shareLink("filename.pdf"))
            .thenReturn("https://sharedlink.com/filename.pdf");
        setupValidToken("user@example.com");

        var builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("files/sample-1mb.pdf"));
        var spec = buildClient(port).post()
            .uri("/files")
            .header("Authorization", "Bearer " + INVALID_TOKEN)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange();
        validateUnauthorized(spec);
    }
}
