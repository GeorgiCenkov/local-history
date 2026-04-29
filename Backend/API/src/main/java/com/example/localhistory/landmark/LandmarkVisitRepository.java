package com.example.localhistory.landmark;

import com.example.localhistory.landmark.model.LandmarkVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandmarkVisitRepository extends JpaRepository<LandmarkVisit, Long> {
    List<LandmarkVisit> findByLandmarkId(Long landmarkId);
}
