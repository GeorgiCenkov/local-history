package com.example.localhistory.quiz.dto.response;

import lombok.Data;

import java.util.List;

// Public quiz dtos do not contain an answer
@Data
public class PublicQuizQuestionDTO {
    private Long id;
    private String question;
    private List<String> options;
}
