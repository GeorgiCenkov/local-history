package com.example.localhistory.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizCompletionRepository extends JpaRepository<QuizCompletion, Long> {
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
}
