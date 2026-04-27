package com.example.localhistory.user.model;

import com.example.localhistory.landmark.model.LandmarkVisit;
import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<LandmarkVisit> visits;

    // Game stats ( lvl, xp, etc. )
    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer points;

    // The points required to level up
    @Column(nullable = false)
    private Integer pointsRequired;

}