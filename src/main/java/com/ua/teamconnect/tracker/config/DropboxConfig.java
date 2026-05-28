package com.ua.teamconnect.tracker.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

@Configuration
public class DropboxConfig {

    @Bean
    @Lazy
    public DbxClientV2 dbxClient(Environment environment) {
        var appName = environment.getRequiredProperty("dropbox.app-name");
        var refreshToken = environment.getRequiredProperty("dropbox.refresh-token");
        var appKey = environment.getRequiredProperty("dropbox.app-key");
        var appSecret = environment.getRequiredProperty("dropbox.app-secret");
        var config = DbxRequestConfig.newBuilder(appName).build();
        var credential = new DbxCredential(
            "accessToken", System.currentTimeMillis(),
            refreshToken, appKey, appSecret
        );
        return new DbxClientV2(config, credential);
    }
}
