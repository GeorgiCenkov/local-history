package com.example.localhistory.supabase.dto.request;

public record CreateUploadUrlRequest(
        String fileName,
        String contentType
) {}