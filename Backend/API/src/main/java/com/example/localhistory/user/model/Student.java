package com.example.localhistory.user.model;

import com.example.localhistory.landmark.model.LandmarkVisit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
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

    @PrePersist
    private void onCreate(){
        this.level = 1;
        this.points = 0;
        this.pointsRequired = 100;
    }

}