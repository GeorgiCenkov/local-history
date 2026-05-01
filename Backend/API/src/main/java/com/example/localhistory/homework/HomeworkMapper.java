package com.example.localhistory.homework;

import com.example.localhistory.homework.dto.response.HomeworkAssignmentDTO;
import com.example.localhistory.homework.dto.response.HomeworkDTO;
import com.example.localhistory.homework.model.Homework;
import com.example.localhistory.homework.model.HomeworkAssignment;
import com.example.localhistory.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Converts homework entities into API DTOs, including computed assignment state.
@Component
@RequiredArgsConstructor
public class HomeworkMapper {

    private final UserMapper userMapper;

    /** Builds the teacher-facing homework DTO with nested IDs flattened for the client. */
    public HomeworkDTO toDTO(Homework homework) {
        HomeworkDTO dto = new HomeworkDTO();
        dto.setId(homework.getId());
        dto.setTitle(homework.getTitle());
        dto.setDescription(homework.getDescription());
        dto.setDueDate(homework.getDueDate());
        dto.setDateCreated(homework.getDateCreated());
        dto.setRequireVisit(homework.getRequireVisit());
        dto.setRequireQuiz(homework.getRequireQuiz());
        dto.setLandmarkId(homework.getLandmark().getId());
        dto.setLandmarkTitle(homework.getLandmark().getTitle());
        dto.setTeacherId(homework.getTeacher().getId());
        dto.setAssignmentsCount(homework.getAssignments() != null ? homework.getAssignments().size() : 0);
        return dto;
    }

    /** Builds the student assignment DTO after the service calculates completion flags. */
    public HomeworkAssignmentDTO toAssignmentDTO(
            HomeworkAssignment assignment,
            boolean visitCompleted,
            boolean quizCompleted) {
        Homework homework = assignment.getHomework();
        boolean requirementsSatisfied = (!homework.getRequireVisit() || visitCompleted)
                && (!homework.getRequireQuiz() || quizCompleted);
        boolean completed = assignment.getCompletedAt() != null;

        HomeworkAssignmentDTO dto = new HomeworkAssignmentDTO();
        dto.setId(assignment.getId());
        dto.setHomeworkId(homework.getId());
        dto.setTitle(homework.getTitle());
        dto.setDescription(homework.getDescription());
        dto.setDueDate(homework.getDueDate());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setCompletedAt(assignment.getCompletedAt());
        dto.setLandmarkId(homework.getLandmark().getId());
        dto.setLandmarkTitle(homework.getLandmark().getTitle());
        dto.setRequireVisit(homework.getRequireVisit());
        dto.setRequireQuiz(homework.getRequireQuiz());
        dto.setVisitCompleted(visitCompleted);
        dto.setQuizCompleted(quizCompleted);
        dto.setRequirementsSatisfied(requirementsSatisfied);
        dto.setCompleted(completed);
        dto.setOverdue(!completed && homework.getDueDate().isBefore(LocalDate.now()));
        dto.setStudent(userMapper.toDto(assignment.getStudent()));
        return dto;
    }
}
