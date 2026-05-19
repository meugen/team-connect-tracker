package com.ua.teamconnect.tracker.service.adapter.storage;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.sharing.RequestedLinkAccessLevel;
import com.dropbox.core.v2.sharing.RequestedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import com.ua.teamconnect.tracker.model.dto.UploadedFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
class DropboxStorageAdapter implements StorageAdapter {

    private static final String ENV_APPNAME = "DROPBOX_APPNAME";
    private static final String ENV_REFRESH_TOKEN = "DROPBOX_REFRESH_TOKEN";
    private static final String ENV_APP_KEY = "DROPBOX_APP_KEY";
    private static final String ENV_APP_SECRET = "DROPBOX_APP_SECRET";

    static Optional<StorageAdapter> build(Environment env) {
        var appName = env.getProperty(ENV_APPNAME);
        var refreshToken = env.getProperty(ENV_REFRESH_TOKEN);
        var appKey = env.getProperty(ENV_APP_KEY);
        var appSecret = env.getProperty(ENV_APP_SECRET);
        if (isEmpty(appName) || isEmpty(refreshToken)
            || isEmpty(appKey) || isEmpty(appSecret)
        ) {
            return Optional.empty();
        }

        var config = DbxRequestConfig.newBuilder(appName).build();
        var credential = new DbxCredential(
            "accessToken", System.currentTimeMillis(),
            refreshToken, appKey, appSecret
        );
        var client = new DbxClientV2(config, credential);
        return Optional.of(new DropboxStorageAdapter(client));
    }

    private final DbxClientV2 client;

    @Override
    public UploadedFileDto upload(String email, MultipartFile file) {
        try {
            var folder = email.replace("@", "_at_");
            var filename = String.format("/%s/%s", folder, file.getOriginalFilename());
            client.refreshAccessToken();
            var metadata = client.files().uploadBuilder(filename)
                .uploadAndFinish(file.getInputStream());
            var settings = SharedLinkSettings.newBuilder()
                .withAccess(RequestedLinkAccessLevel.VIEWER)
                .withAllowDownload(true)
                .withRequestedVisibility(RequestedVisibility.PUBLIC)
                .build();
            var link = client.sharing().createSharedLinkWithSettings(metadata.getPathLower(), settings);
            return new UploadedFileDto(link.getUrl());
        } catch (Exception e) {
            var message = e.getMessage();
            message = isEmpty(message) ? "File upload is failed" : message;
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }
}
