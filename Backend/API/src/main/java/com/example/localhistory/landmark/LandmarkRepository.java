package com.example.localhistory.landmark;

import com.example.localhistory.landmark.model.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {
    List<Landmark> findByOwnerEmail(String ownerEmail);

    Optional<Landmark> findByIdAndOwnerEmail(Long id, String ownerEmail);
}
