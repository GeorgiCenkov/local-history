package com.example.localhistory.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByLandmarkOwnerEmail(String ownerEmail);

    Optional<Quiz> findByLandmarkId(Long landmarkId);

    Optional<Quiz> findByIdAndLandmarkOwnerEmail(Long id, String ownerEmail);

    boolean existsByLandmarkId(Long landmarkId);
}
