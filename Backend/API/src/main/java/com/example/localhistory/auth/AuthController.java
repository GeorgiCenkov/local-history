package com.example.localhistory.auth;

import com.example.localhistory.auth.dto.response.AuthResponse;
import com.example.localhistory.auth.request.LoginRequest;
import com.example.localhistory.auth.request.RefreshTokenRequest;
import com.example.localhistory.token.service.TokenService;
import com.example.localhistory.user.dto.request.UserCreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    // POST /api/auth
    @PostMapping
    public ResponseEntity<AuthResponse> create(@Valid @RequestBody UserCreateRequest model) {
        AuthResponse response = authService.createUser(model);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest model) {
        AuthResponse response = authService.login(model);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshLogin(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        tokenService.invalidateToken(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }
}