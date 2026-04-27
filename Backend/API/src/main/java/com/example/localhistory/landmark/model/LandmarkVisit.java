package com.example.localhistory.landmark.model;

import com.example.localhistory.coordinates.Coordinates;
import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "landmark_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LandmarkVisit {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private Student user; // the user who visited the landmark

    @ManyToOne(optional = false)
    @JoinColumn(name = "landmark_id")
    private Landmark landmark;

    @Column(nullable = false)
    private LocalDateTime dateVisited;

    @Column(nullable = false)
    private String image; // Image proof of the visit

    // The exact coordinates of the visit / photo
    @Embedded
    private Coordinates coordinates;

    @PrePersist
    private void onCreate() {
        this.dateVisited = LocalDateTime.now();
    }
}
