package com.example.localhistory.user;

import com.example.localhistory.landmarkvisit.LandmarkVisit;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 20)
    private String firstName;

    @Column(nullable = false, length = 20)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime birthDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<LandmarkVisit> visits;

    protected User() {}

    public User(String firstName, String lastName, String email, String passwordHash, LocalDateTime birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.birthDate = birthDate;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }
}
