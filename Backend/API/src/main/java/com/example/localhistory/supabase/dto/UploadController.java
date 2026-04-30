package com.example.localhistory.supabase.dto;

import com.example.localhistory.supabase.dto.request.CreateUploadUrlRequest;
import com.example.localhistory.supabase.dto.response.CreateUploadUrlResponse;
import com.example.localhistory.supabase.dto.service.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final SupabaseStorageService supabaseStorageService;

    @PostMapping("/signed-url")
    public CreateUploadUrlResponse createSignedUrl(
            @RequestBody CreateUploadUrlRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        return supabaseStorageService.createSignedUploadUrl(
                email,
                request.fileName(),
                request.contentType()
        );
    }
}