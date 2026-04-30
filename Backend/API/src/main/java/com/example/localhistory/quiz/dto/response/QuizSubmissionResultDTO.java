package com.example.localhistory.quiz.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizSubmissionResultDTO {
    private Long quizId;
    private int totalQuestions;
    private int answeredQuestions;
    private int correctAnswers;
    private int awardedPoints;
    private List<QuizQuestionResultDTO> results;
}
