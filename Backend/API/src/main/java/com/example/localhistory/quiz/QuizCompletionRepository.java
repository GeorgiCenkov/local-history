package com.example.localhistory.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface QuizCompletionRepository extends JpaRepository<QuizCompletion, Long> {
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);

    boolean existsByStudentIdAndQuizIdAndCompletedAtLessThanEqual(
            Long studentId,
            Long quizId,
            LocalDateTime deadline);
}
