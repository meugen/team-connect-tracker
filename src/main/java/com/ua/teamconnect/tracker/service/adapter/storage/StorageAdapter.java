package com.ua.teamconnect.tracker.service.adapter.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageAdapter {

    String upload(String email, MultipartFile file);

    String shareLink(String filename);
}
