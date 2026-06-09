package com.ua.teamconnect.tracker.service.storage;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.RequestedLinkAccessLevel;
import com.dropbox.core.v2.sharing.RequestedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Callable;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class DropboxStorageService {

    private final DbxClientV2 client;

    public DropboxStorageService(@Lazy DbxClientV2 client) {
        this.client = client;
    }

    public String upload(String email, MultipartFile file) {
        return retry(() -> {
            var folder = getFolderFromEmail(email);
            var fileName = sanitizeFilename(file.getOriginalFilename());
            var dropboxPath = "/%s/%s".formatted(folder, fileName);

            var metadata = client.files()
                .uploadBuilder(dropboxPath)
                .withMode(WriteMode.OVERWRITE)
                .uploadAndFinish(file.getInputStream());

            return metadata.getPathLower();
        });
    }

    public String shareLink(String dropboxPath) {
        return retry(() -> {
            var settings = SharedLinkSettings.newBuilder()
                .withAccess(RequestedLinkAccessLevel.VIEWER)
                .withAllowDownload(true)
                .withRequestedVisibility(RequestedVisibility.PUBLIC)
                .build();

            var link = client.sharing()
                .createSharedLinkWithSettings(dropboxPath, settings);

            return link.getUrl();
        });
    }
    
    public void delete(String fileName) {
        retry(() -> {
            client.files().deleteV2(fileName);
            return null;
        });
        
    }

    private String getFolderFromEmail(String email) {
        return email
            .trim()
            .toLowerCase()
            .split("@")[0]
            .replaceAll("[^a-z0-9._-]", "_");
    }

    private String sanitizeFilename(String filename) {

        if (filename == null || filename.isBlank()) {
            return "file";
        }

        filename = filename.replace("\\", "/");
        filename = filename.substring(filename.lastIndexOf('/') + 1);
        filename = filename.replaceAll("[:*?\"<>|]", "_");

        var lastDotIndex = filename.lastIndexOf('.');

        if (lastDotIndex <= 0) {
            return filename.length() > 100
                ? filename.substring(0, 100)
                : filename;
        }

        var name = filename.substring(0, lastDotIndex);
        var extension = filename.substring(lastDotIndex + 1);

        if (name.length() > 100) {
            name = name.substring(0, 100);
        }

        return name + "." + extension;
    }

    private <T> T retry(Callable<T> callable) {
        try {
            try {
                return callable.call();
            } catch (Exception e) {
                client.refreshAccessToken();
                return callable.call();
            }
        } catch (Exception e) {
            var message = isEmpty(e.getMessage())
                ? "Storage operation failed"
                : e.getMessage();

            throw new IllegalStateException(message, e);
        }
    }
    
    public boolean isDropboxNotFound(Exception e) {
        Throwable current = e;
        
        while (current != null) {
            if (current instanceof DeleteErrorException deleteErrorException) {
                return deleteErrorException.errorValue.isPathLookup()
                    && deleteErrorException.errorValue.getPathLookupValue().isNotFound();
            }

            current = current.getCause();
        }
        return false;
    }
}
