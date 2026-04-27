package com.example.localhistory.token;

import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final TokenRepository tokenRepositor;

    public TokenService(TokenRepository tokenRepositor) {
        this.tokenRepositor = tokenRepositor;
    }
}
