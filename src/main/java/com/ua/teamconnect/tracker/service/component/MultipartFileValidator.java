package com.ua.teamconnect.tracker.service.component;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.ObjectUtils.isEmpty;

@Component
public class MultipartFileValidator {

    private static final Map<String, Set<String>> ALLOWED_MIME_TYPES = Map.of(
        "png", Set.of("image/png"),
        "jpg", Set.of("image/jpeg"),
        "jpeg", Set.of("image/jpeg"),
        "pdf", Set.of("application/pdf"),
        "doc", Set.of("application/msword"),
        "docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    );

    public void validate(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) throw new IllegalArgumentException("File is empty");
        String extension = extractExtension(multipartFile.getOriginalFilename());
        String mimeType = normalizeMimeType(multipartFile.getContentType());
        if (!isAllowedExtensionAndMimeType(extension, mimeType)) {
            throw new IllegalArgumentException("File extension or MIME type is not allowed");
        }
    }

    private String extractExtension(String fileName) {
        if (isEmpty(fileName)) return null;

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizeMimeType(String contentType) {
        if (isEmpty(contentType)) return null;

        String normalized = contentType.toLowerCase(Locale.ROOT).trim();
        int semicolonIndex = normalized.indexOf(';');
        if (semicolonIndex >= 0) {
            return normalized.substring(0, semicolonIndex).trim();
        }
        return normalized;
    }

    private boolean isAllowedExtensionAndMimeType(String extension, String mimeType) {
        if (extension == null || mimeType == null) {
            return false;
        }

        Set<String> allowedMimeTypes = ALLOWED_MIME_TYPES.get(extension);
        return allowedMimeTypes != null && allowedMimeTypes.contains(mimeType);
    }
}
