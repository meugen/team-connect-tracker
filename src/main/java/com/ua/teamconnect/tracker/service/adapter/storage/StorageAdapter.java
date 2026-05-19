package com.ua.teamconnect.tracker.service.adapter.storage;

import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import org.springframework.web.multipart.MultipartFile;

public interface StorageAdapter {

    UploadedFileDto upload(String email, MultipartFile file);
}
