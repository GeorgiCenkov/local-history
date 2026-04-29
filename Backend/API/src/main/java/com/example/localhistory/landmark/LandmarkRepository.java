package com.example.localhistory.landmark;

import com.example.localhistory.landmark.model.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {
}
