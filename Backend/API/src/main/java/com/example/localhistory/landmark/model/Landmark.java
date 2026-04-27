package com.example.localhistory.landmark.model;

import com.example.localhistory.coordinates.Coordinates;
import com.example.localhistory.quiz.Quiz;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

// Represents landmarks of interest in the game
@Entity
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

    // The amount of XP given to users who visit the landmark
    @Column(nullable = false)
    private Integer visitRewardPoints;

    @OneToMany(mappedBy = "landmark")
    private List<LandmarkVisit> visits;

    @OneToOne(mappedBy = "landmark")
    private Quiz quiz;

    protected Landmark() {

    }

    public Landmark(Long id, String title, String description, LocalDateTime dateCreated, String imageUrl, Coordinates coordinates, Integer visitRewardPoints) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateCreated = dateCreated;
        this.imageUrl = imageUrl;
        this.coordinates = coordinates;
        this.visitRewardPoints = visitRewardPoints;
    }

    @PrePersist
    private void onCreate() {
        this.dateCreated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Integer getVisitRewardPoints() {
        return visitRewardPoints;
    }

    public List<LandmarkVisit> getVisits() {
        return visits;
    }
}
