package com.example.localhistory.homework.dto.response;

import com.example.localhistory.user.dto.response.StudentDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Response sent for homework assigned to a specific student, including current completion state.
@Data
public class HomeworkAssignmentDTO {
    private Long id;
    private Long homeworkId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private Long landmarkId;
    private String landmarkTitle;
    private Boolean requireVisit;
    private Boolean requireQuiz;
    private Boolean visitCompleted;
    private Boolean quizCompleted;
    private Boolean requirementsSatisfied;
    private Boolean completed;
    private Boolean overdue;
    private StudentDTO student;
}
