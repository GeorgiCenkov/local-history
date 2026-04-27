package com.example.localhistory.user.dto.response;

import com.example.localhistory.user.model.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public abstract class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private Role role;
}