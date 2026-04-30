package com.example.localhistory.quiz.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizDTO {
    private Long id;
    private String title;
    private Long landmarkId;
    private List<QuizQuestionDTO> questions;
}
