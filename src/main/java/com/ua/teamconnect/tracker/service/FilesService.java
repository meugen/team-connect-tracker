package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import com.ua.teamconnect.tracker.service.component.MultipartFileValidator;
import com.ua.teamconnect.tracker.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final MultipartFileValidator validator;
    private final StorageService storageService;

    public UploadedFileDto uploadFile(String email, MultipartFile file) {
        validator.validate(file);
        var filename = storageService.upload(email, file);
        var url = storageService.shareLink(filename);
        return new UploadedFileDto(url);
    }
}
