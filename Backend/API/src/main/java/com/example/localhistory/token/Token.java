package com.example.localhistory.token;

import com.example.localhistory.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// Store JWT refresh tokens for users
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Integer id;

    // the refresh token stored in db
    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean isRevoked;

    private LocalDateTime revokedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Not stored in db, in memory jwt token
    @Transient
    private String jwtToken;

    @Transient
    public boolean isExpired() {
        return this.expirationDate.isAfter(LocalDateTime.now());
    }

    @PrePersist
    private void onCreate() {
        this.isRevoked = false;
        this.createdAt = LocalDateTime.now();
    }

    public Token() {

    }

    public Token(User user, String refreshToken, LocalDateTime expirationDate) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
    }
    public Integer getId() {
        return id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public User getUser() {
        return user;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}
