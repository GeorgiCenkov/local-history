    package com.example.localhistory.user.model;

    import com.example.localhistory.token.Token;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.time.LocalDate;
    import java.util.List;

    @Entity
    @Table(name = "users")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "user_type")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public abstract class User {

        @Id
        @GeneratedValue
        private Long id;

        @Column(nullable = false, length = 20)
        private String firstName;

        @Column(nullable = false, length = 20)
        private String lastName;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String passwordHash;

        @Column(nullable = false)
        private LocalDate birthDate;

        // A user can have a lot of tokens - active, expired, or revoked
        // Multiple active tokens are used for enabling multi-device login
        @OneToMany(mappedBy = "user")
        private List<Token> tokens;

        // Not mapped helper method
        public Role getRole() {
            if (this instanceof Teacher) return Role.TEACHER;
            if (this instanceof Student) return Role.STUDENT;
            throw new IllegalStateException("Unknown user type: " + this.getClass().getSimpleName());
        }
    }
