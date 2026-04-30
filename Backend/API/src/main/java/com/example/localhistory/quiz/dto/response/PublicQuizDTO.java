package com.example.localhistory.quiz.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PublicQuizDTO {
    private Long id;
    private String title;
    private Long landmarkId;
    private boolean alreadyCompleted;
    private List<PublicQuizQuestionDTO> questions;
}
