package com.example.localhistory.homework.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Response sent for homework definition reads and teacher management screens.
@Data
public class HomeworkDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalDateTime dateCreated;
    private Boolean requireVisit;
    private Boolean requireQuiz;
    private Long landmarkId;
    private String landmarkTitle;
    private Long teacherId;
    private Integer assignmentsCount;
}
