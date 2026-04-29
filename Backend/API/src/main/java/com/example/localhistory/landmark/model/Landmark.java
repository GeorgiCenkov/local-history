package com.example.localhistory.landmark.model;

import com.example.localhistory.coordinates.Coordinates;
import com.example.localhistory.quiz.Quiz;
import com.example.localhistory.user.model.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// Represents landmarks of interest in the game
@Entity
@Table(name = "landmarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Landmark {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private String imageUrl;

    @Embedded
    private Coordinates coordinates;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private Teacher owner;

    // The amount of XP given to users who visit the landmark
    @Column(nullable = false)
    private Integer visitRewardPoints;

    @OneToMany(mappedBy = "landmark")
    private List<LandmarkVisit> visits;

    @OneToOne(mappedBy = "landmark")
    private Quiz quiz;

    @PrePersist
    private void onCreate() {
        this.dateCreated = LocalDateTime.now();
    }
}
