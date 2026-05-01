package com.example.localhistory.homework;

import com.example.localhistory.homework.model.HomeworkAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repository for student assignment rows created from homework definitions.
public interface HomeworkAssignmentRepository extends JpaRepository<HomeworkAssignment, Long> {
    List<HomeworkAssignment> findByHomeworkId(Long homeworkId);

    List<HomeworkAssignment> findByHomeworkIdAndHomeworkTeacherEmail(Long homeworkId, String teacherEmail);

    List<HomeworkAssignment> findByStudentEmail(String studentEmail);

    void deleteByHomeworkLandmarkId(Long landmarkId);

    Optional<HomeworkAssignment> findByIdAndStudentEmail(Long id, String studentEmail);

    Optional<HomeworkAssignment> findByHomeworkIdAndStudentId(Long homeworkId, Long studentId);

    boolean existsByHomeworkIdAndStudentId(Long homeworkId, Long studentId);
}
