package com.example.localhistory.homework.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

// Request body used by teachers to create or replace a homework task.
@Data
public class HomeworkRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 300, message = "Description must not exceed 300 characters")
    private String description;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;

    @NotNull(message = "Landmark ID is required")
    private Long landmarkId;

    @NotNull(message = "Visit requirement is required")
    private Boolean requireVisit;

    @NotNull(message = "Quiz requirement is required")
    private Boolean requireQuiz;
}
