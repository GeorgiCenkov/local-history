package com.example.localhistory.homework.model;

import com.example.localhistory.landmark.model.Landmark;
import com.example.localhistory.user.model.Teacher;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Represents a teacher-created task tied to visiting a landmark and/or completing its quiz.
@Entity
@Table(name = "homework")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "homework_seq")
    @SequenceGenerator(name = "homework_seq", sequenceName = "homework_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private Boolean requireVisit;

    @Column(nullable = false)
    private Boolean requireQuiz;

    @ManyToOne(optional = false)
    @JoinColumn(name = "landmark_id", nullable = false)
    private Landmark landmark;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomeworkAssignment> assignments;

    @PrePersist
    private void onCreate() {
        this.dateCreated = LocalDateTime.now();
    }
}
