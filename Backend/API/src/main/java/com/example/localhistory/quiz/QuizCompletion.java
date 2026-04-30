package com.example.localhistory.quiz;

import com.example.localhistory.user.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "quiz_completions",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_quiz_completions_student_quiz",
                columnNames = {"student_id", "quiz_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizCompletion {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private Integer awardedPoints;

    @PrePersist
    private void onCreate() {
        this.completedAt = LocalDateTime.now();
    }
}
