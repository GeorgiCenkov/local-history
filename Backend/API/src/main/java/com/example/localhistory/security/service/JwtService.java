package com.example.localhistory.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private Key getKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    /**
     * Generates a signed access token containing the user's ID and role.
     * The role is stored as a standard "role" claim so the filter can
     * reconstruct authorities without a database lookup on every request.
     *
     * @param userId the user's database ID (stored as the JWT subject)
     * @param role   the user's role, e.g. "TEACHER" or "STUDENT"
     */
    public String generateAccessToken(String userId, String role) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)          // e.g. "TEACHER"
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh tokens are opaque random strings — they carry no claims and are
     * validated by looking them up in the database, not by parsing.
     */
    public String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        new java.security.SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /** Returns the user ID stored in the token's subject field. */
    public String extractUserId(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Returns the role claim from the token, e.g. "TEACHER".
     * Returns null if the claim is absent (e.g. old tokens issued before this change).
     */
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean isValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}