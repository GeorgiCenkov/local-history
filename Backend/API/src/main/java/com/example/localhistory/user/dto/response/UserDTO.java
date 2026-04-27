package com.example.localhistory.user.dto.response;

import com.example.localhistory.user.model.Role;
import java.time.LocalDate;

public abstract class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private Role role;

    public UserDTO() {}

    public UserDTO(Long id, String firstName, String lastName, String email, LocalDate birthDate, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Role getRole() {
        return role;
    }
}