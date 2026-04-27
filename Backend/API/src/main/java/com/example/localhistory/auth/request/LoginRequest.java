package com.example.localhistory.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}
