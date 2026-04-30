package com.example.localhistory.quiz.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class QuizQuestionRequest {

    @NotBlank(message = "Question is required")
    @Size(max = 200, message = "Question must not exceed 200 characters")
    private String question;

    @NotEmpty(message = "At least two answer options are required")
    @Size(min = 2, message = "At least two answer options are required")
    private List<@NotBlank(message = "Answer option must not be blank") String> options;

    @NotBlank(message = "Correct answer is required")
    @Size(max = 200, message = "Correct answer must not exceed 200 characters")
    private String correctAnswer;
}
