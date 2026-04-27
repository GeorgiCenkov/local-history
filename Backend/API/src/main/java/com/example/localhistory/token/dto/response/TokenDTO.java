package com.example.localhistory.token.dto.response;

import com.example.localhistory.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class TokenDTO {
    private Integer id;
    private String refreshToken;
    private LocalDateTime expirationDate;
    private boolean isRevoked;
    private LocalDateTime revokedAt;
    private LocalDateTime createdAt;
    private String jwtToken;
    public boolean isExpired;
}
