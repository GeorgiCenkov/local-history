package com.example.localhistory.supabase.dto.response;

public record CreateUploadUrlResponse(
        String path,
        String signedUrl,
        String publicUrl
) {}