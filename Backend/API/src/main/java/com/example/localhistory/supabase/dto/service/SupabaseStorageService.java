package com.example.localhistory.supabase.dto.service;

import com.example.localhistory.supabase.dto.response.CreateUploadUrlResponse;
import com.example.localhistory.supabase.dto.response.SupabaseSignedUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.secret-key}")
    private String secretKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestClient restClient = RestClient.create();

    public CreateUploadUrlResponse createSignedUploadUrl(
            String email,
            String originalFileName,
            String contentType
    ) {
        validateImage(contentType);

        String extension = getExtension(originalFileName);

        String safeEmail = email.replaceAll("[^a-zA-Z0-9._-]", "_");

        String path = "users/%s/%s.%s".formatted(
                safeEmail,
                UUID.randomUUID(),
                extension
        );

        String endpoint = "%s/storage/v1/object/upload/sign/%s/%s"
                .formatted(supabaseUrl, bucket, path);

        SupabaseSignedUploadResponse response = restClient.post()
                .uri(endpoint)
                .header("Authorization", "Bearer " + secretKey)
                .header("apikey", secretKey)
                .header("Content-Type", "application/json")
                .body(Map.of("upsert", false))
                .retrieve()
                .body(SupabaseSignedUploadResponse.class);

        if (response == null || response.url() == null) {
            throw new IllegalStateException("Failed to create signed upload URL from Supabase");
        }

        String signedUrl = response.url().startsWith("/storage/v1")
                ? supabaseUrl + response.url()
                : supabaseUrl + "/storage/v1" + response.url();

        String publicUrl = "%s/storage/v1/object/public/%s/%s"
                .formatted(supabaseUrl, bucket, path);

        return new CreateUploadUrlResponse(path, signedUrl, publicUrl);
    }

    private void validateImage(String contentType) {
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are allowed");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}