package com.example.localhistory.landmark;

import com.example.localhistory.landmark.model.LandmarkVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LandmarkVisitRepository extends JpaRepository<LandmarkVisit, Long> {
    List<LandmarkVisit> findByLandmarkId(Long landmarkId);

    List<LandmarkVisit> findByLandmarkIdAndLandmarkOwnerEmail(Long landmarkId, String ownerEmail);

    Optional<LandmarkVisit> findByIdAndLandmarkOwnerEmail(Long id, String ownerEmail);

    boolean existsByUserIdAndLandmarkId(Long userId, Long landmarkId);
}
