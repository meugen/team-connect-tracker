package com.ua.teamconnect.tracker.service.adapter.storage;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class AwsS3StorageAdapter implements StorageAdapter {

    private static final String ENV_BUCKET = "AWS_S3_BUCKET";
    private static final String ENV_REGION = "AWS_REGION";
    private static final String ENV_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    private static final String ENV_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";

    public static Optional<StorageAdapter> build(Environment env) {
        var bucket = env.getProperty(ENV_BUCKET);
        var region = env.getProperty(ENV_REGION);
        var accessKeyId = env.getProperty(ENV_ACCESS_KEY_ID);
        var secretAccessKey = env.getProperty(ENV_SECRET_ACCESS_KEY);
        if (isEmpty(bucket) || isEmpty(region)|| isEmpty(accessKeyId) || isEmpty(secretAccessKey)) {
            return Optional.empty();
        }

        var credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        var clientBuilder = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(() -> credentials);
        return Optional.of(new AwsS3StorageAdapter(
            clientBuilder.build(),
            bucket,
            region
        ));
    }

    private final S3Client client;
    private final String bucket;
    private final String region;

    @Override
    public String upload(String email, MultipartFile file) {
        var objectKey = objectKey(email, file.getOriginalFilename());
        try {
            var request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(file.getContentType())
                .build();
            client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return objectKey;
        } catch (Exception e) {
            var message = e.getMessage();
            message = isEmpty(message) ? "Failed to upload file to S3" : message;
            throw new IllegalStateException(message, e);
        }
    }

    @Override
    public String shareLink(String filename) {
        var baseUrl = String.format("https://%s.s3.%s.amazonaws.com", bucket, region);
        var pathSegments = filename.split("/");
        return UriComponentsBuilder.fromUriString(baseUrl)
            .pathSegment(pathSegments)
            .build()
            .encode()
            .toUriString();
    }

    private String objectKey(String email, String filename) {
        var folder = email.replace("@", "_at_");
        return folder + "/" + filename;
    }
}
