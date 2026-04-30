package com.example.localhistory.quiz.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizQuestionDTO {
    private Long id;
    private String question;
    private List<String> options;
    private String correctAnswer;
}
