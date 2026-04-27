package com.example.localhistory.token.service;

import com.example.localhistory.security.service.JwtService;
import com.example.localhistory.token.Token;
import com.example.localhistory.token.TokenMapper;
import com.example.localhistory.token.TokenRepository;
import com.example.localhistory.token.dto.response.TokenDTO;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final TokenMapper mapper;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration-days}")
    private int refreshExpirationDays;

    public TokenService(TokenRepository tokenRepository, UserRepository userRepository, TokenMapper mapper, JwtService jwtService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.jwtService = jwtService;
    }

    // Only called internally so we can assume userId is valid
    public TokenDTO createTokenAsync(Long userId) {

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(userId.toString());
        String refreshToken = jwtService.generateRefreshToken(userId.toString());

        if (refreshExpirationDays <= 0) {
            throw new IllegalArgumentException("Invalid JWT configuration: refresh-expiration-days");
        }

        User userRef = userRepository.getReferenceById(userId);
        Token token = new Token(userRef, accessToken, refreshToken, LocalDateTime.now().plusDays(refreshExpirationDays));

        // multi-device support - no invalidation
        tokenRepository.save(token);

        return mapper.toDTO(token);
    }

    @Transactional
    public void invalidateToken(String refreshToken) {

        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElse(null);

        // token does not exist - nothing to revoke
        if (token == null) return;

        token.setRevoked(true);
        token.setRevokedAt(LocalDateTime.now());

        tokenRepository.save(token);
    }

    @Transactional
    public Token refreshToken(String refreshTokenValue) {

        Token token = tokenRepository.findByRefreshToken(refreshTokenValue)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token")
                );

        if (token.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        if (token.isRevoked()) {
            // If the token is revoked, revoke all other user tokens for security
            List<Token> tokens = tokenRepository.findAllByUserIdAndIsRevokedFalse(token.getUser().getId());

            for (Token t : tokens) {
                t.setRevoked(true);
                t.setRevokedAt(LocalDateTime.now());
            }

            tokenRepository.saveAll(tokens);

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token revoked. All sessions invalidated."
            );
        }

        // Generate a new refresh token for the future
        token.setRefreshToken(jwtService.generateRefreshToken(token.getUser().getId().toString()));
        token.setExpirationDate(LocalDateTime.now().plusDays(refreshExpirationDays));

        // access token is NOT stored, but is held in memory to be sent to frontend
        String newAccessToken = jwtService.generateAccessToken(token.getUser().getId().toString());
        token.setJwtToken(newAccessToken); // transient

        tokenRepository.save(token);

        return token;
    }
}
