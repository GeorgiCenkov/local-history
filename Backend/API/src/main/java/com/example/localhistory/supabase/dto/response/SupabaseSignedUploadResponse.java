package com.example.localhistory.supabase.dto.response;

public record SupabaseSignedUploadResponse(
        String url,
        String path,
        String token
) {}