package com.ua.teamconnect.tracker.service;

import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import com.ua.teamconnect.tracker.model.entity.MediaFile;
import com.ua.teamconnect.tracker.repository.MediaFileRepository;
import com.ua.teamconnect.tracker.service.component.MultipartFileValidator;
import com.ua.teamconnect.tracker.service.storage.DropboxStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MediaFileService {

    private final MultipartFileValidator validator;
    private final DropboxStorageService dropboxStorageService;
    private final MediaFileRepository mediaFileRepository;

    public UploadedFileDto uploadFile(String email, MultipartFile file) {
        validator.validate(file);
        var fileName = dropboxStorageService.upload(email, file);
        var url = dropboxStorageService.shareLink(fileName);
        var mediaFile = new MediaFile();
        
        mediaFile.setContentType(file.getContentType());
        mediaFile.setDropboxPath(fileName);
        mediaFile.setUrl(url);
        mediaFile.setSize(file.getSize());  
        mediaFileRepository.save(mediaFile);
        
        return new UploadedFileDto(url);
    }
}
