package com.example.localhistory.homework.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

// Request body for assigning one homework task to many students at once.
@Data
public class HomeworkAssignmentRequest {

    @NotEmpty(message = "At least one student ID is required")
    private List<Long> studentIds;
}
