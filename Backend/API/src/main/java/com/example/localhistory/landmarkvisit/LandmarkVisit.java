package com.example.localhistory.landmarkvisit;

import com.example.localhistory.coordinates.Coordinates;
import com.example.localhistory.landmark.Landmark;
import com.example.localhistory.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class LandmarkVisit {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user; // the user who visited the landmark

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

    protected LandmarkVisit() {

    }

    public LandmarkVisit(Long id, User user, Landmark landmark, LocalDateTime dateVisited, String image, Coordinates coordinates) {
        this.id = id;
        this.user = user;
        this.landmark = landmark;
        this.dateVisited = dateVisited;
        this.image = image;
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getImage() {
        return image;
    }

    public LocalDateTime getDateVisited() {
        return dateVisited;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }
}
