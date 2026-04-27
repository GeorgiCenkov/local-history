package com.example.localhistory.auth.dto.response;

import com.example.localhistory.token.dto.response.TokenDTO;
import com.example.localhistory.user.dto.response.UserDTO;

public record AuthResponse(UserDTO user, TokenDTO token) {
}
