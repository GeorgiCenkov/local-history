package com.example.localhistory.quiz.dto.response;

import lombok.Data;

@Data
public class QuizQuestionResultDTO {
    private Long questionId;
    private String submittedAnswer;
    private String correctAnswer;
    private boolean correct;
}
