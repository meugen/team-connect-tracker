package com.ua.teamconnect.tracker.service.adapter.storage;

import org.springframework.web.multipart.MultipartFile;

class NotConfiguredStorageAdapter implements StorageAdapter {

    @Override
    public String upload(String email, MultipartFile file) {
        throw new IllegalStateException("Storage adapter is not configured");
    }

    @Override
    public String shareLink(String filename) {
        throw new IllegalStateException("Storage adapter is not configured");
    }
}
