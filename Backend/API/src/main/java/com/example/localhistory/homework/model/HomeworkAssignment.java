package com.example.localhistory.homework.model;

import com.example.localhistory.user.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Links one homework task to one student so teachers can assign the same task to many students.
@Entity
@Table(
        name = "homework_assignments",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_homework_assignments_homework_student",
                columnNames = {"homework_id", "student_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "homework_assignments_seq")
    @SequenceGenerator(name = "homework_assignments_seq", sequenceName = "homework_assignments_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    @Column
    private LocalDateTime completedAt;

    @PrePersist
    private void onCreate() {
        this.assignedAt = LocalDateTime.now();
    }
}
