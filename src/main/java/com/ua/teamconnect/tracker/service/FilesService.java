package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import com.ua.teamconnect.tracker.service.adapter.storage.StorageAdapter;
import com.ua.teamconnect.tracker.service.component.MultipartFileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final MultipartFileValidator validator;
    private final StorageAdapter storageAdapter;

    public UploadedFileDto uploadFile(String email, MultipartFile file) {
        validator.validate(file);
        var filename = storageAdapter.upload(email, file);
        var url = storageAdapter.shareLink(filename);
        return new UploadedFileDto(url);
    }
}
