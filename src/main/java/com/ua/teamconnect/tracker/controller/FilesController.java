package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.annotation.ApiResponseBadRequest;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseOk;
import com.ua.teamconnect.tracker.model.annotation.ApiResponseUnauthorized;
import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import com.ua.teamconnect.tracker.service.FilesService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@ApiResponseOk @ApiResponseUnauthorized
@Tag(name = "Files Controller", description = "Endpoints related to file management")
public class FilesController {

    private final FilesService filesService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponseBadRequest
    public UploadedFileDto uploadFile(
        @AuthenticationPrincipal Jwt jwt,
        @Parameter(description = "File to upload", required = true)
        @RequestParam MultipartFile file
    ) {
        return filesService.uploadFile(jwt.getSubject(), file);
    }
}
