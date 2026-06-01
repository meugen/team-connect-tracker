package com.ua.teamconnect.tracker.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(String email, MultipartFile file);

    String shareLink(String dropboxPath);
}
