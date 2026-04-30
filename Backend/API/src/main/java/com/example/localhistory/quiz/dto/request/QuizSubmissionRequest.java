package com.example.localhistory.quiz.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmissionRequest {

    @NotEmpty(message = "At least one answer is required")
    @Valid
    private List<QuizQuestionAnswerRequest> answers;
}
