package com.example.localhistory.token;

import com.example.localhistory.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Store JWT refresh tokens for users
@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue
    private Integer id;

    // the refresh token stored in db
    @Column(nullable = false, unique = true)
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

    public Token(User user, String accessToken, String refreshToken, LocalDateTime expirationDate) {
        this.refreshToken = refreshToken;
        this.expirationDate = expirationDate;
        this.user = user;
        this.jwtToken = accessToken;
    }

    @Transient
    public boolean isExpired() {
        return this.expirationDate.isBefore(LocalDateTime.now());
    }

    @PrePersist
    private void onCreate() {
        this.isRevoked = false;
        this.createdAt = LocalDateTime.now();
    }
}
