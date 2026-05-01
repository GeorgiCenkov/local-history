package com.example.localhistory.homework;

import com.example.localhistory.homework.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repository for teacher-owned homework definitions.
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByTeacherEmail(String teacherEmail);

    Optional<Homework> findByIdAndTeacherEmail(Long id, String teacherEmail);
}
