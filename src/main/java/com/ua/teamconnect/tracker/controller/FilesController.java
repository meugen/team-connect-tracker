package com.ua.teamconnect.tracker.controller;

import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import com.ua.teamconnect.tracker.service.FilesService;
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
public class FilesController {

    private final FilesService filesService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadedFileDto uploadFile(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam MultipartFile file
    ) {
        return filesService.uploadFile(jwt.getSubject(), file);
    }
}
