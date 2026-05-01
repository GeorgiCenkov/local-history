package com.example.localhistory.homework;

import com.example.localhistory.homework.dto.request.HomeworkAssignmentRequest;
import com.example.localhistory.homework.dto.request.HomeworkRequest;
import com.example.localhistory.homework.dto.response.HomeworkAssignmentDTO;
import com.example.localhistory.homework.dto.response.HomeworkDTO;
import com.example.localhistory.homework.model.Homework;
import com.example.localhistory.homework.model.HomeworkAssignment;
import com.example.localhistory.landmark.LandmarkRepository;
import com.example.localhistory.landmark.LandmarkVisitRepository;
import com.example.localhistory.landmark.model.Landmark;
import com.example.localhistory.quiz.QuizCompletionRepository;
import com.example.localhistory.quiz.QuizRepository;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.Teacher;
import com.example.localhistory.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

// Business logic for teacher homework CRUD and student homework assignment status.
@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final HomeworkAssignmentRepository assignmentRepository;
    private final LandmarkRepository landmarkRepository;
    private final LandmarkVisitRepository landmarkVisitRepository;
    private final QuizRepository quizRepository;
    private final QuizCompletionRepository quizCompletionRepository;
    private final UserRepository userRepository;
    private final HomeworkMapper mapper;

    /** Returns every homework task owned by the authenticated teacher. */
    @Transactional(readOnly = true)
    public List<HomeworkDTO> getTeacherHomework(String teacherEmail) {
        return homeworkRepository.findByTeacherEmail(teacherEmail)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /** Fetches one teacher-owned homework task. */
    @Transactional(readOnly = true)
    public HomeworkDTO getTeacherHomeworkById(String teacherEmail, Long id) {
        return mapper.toDTO(findHomeworkForTeacherOrThrow(teacherEmail, id));
    }

    /** Creates a homework task for a teacher-owned landmark. */
    @Transactional
    public HomeworkDTO createHomework(String teacherEmail, HomeworkRequest request) {
        Homework homework = new Homework();
        homework.setTeacher(findTeacherOrThrow(teacherEmail));
        applyRequest(teacherEmail, homework, request);
        return mapper.toDTO(homeworkRepository.save(homework));
    }

    /** Replaces all mutable fields on a teacher-owned homework task. */
    @Transactional
    public HomeworkDTO updateHomework(String teacherEmail, Long id, HomeworkRequest request) {
        Homework homework = findHomeworkForTeacherOrThrow(teacherEmail, id);
        applyRequest(teacherEmail, homework, request);
        return mapper.toDTO(homeworkRepository.save(homework));
    }

    /** Deletes the homework definition and its assignment rows. */
    @Transactional
    public void deleteHomework(String teacherEmail, Long id) {
        homeworkRepository.delete(findHomeworkForTeacherOrThrow(teacherEmail, id));
    }

    /** Assigns one homework task to multiple students in a single request. */
    @Transactional
    public List<HomeworkAssignmentDTO> assignHomework(
            String teacherEmail,
            Long homeworkId,
            HomeworkAssignmentRequest request) {
        Homework homework = findHomeworkForTeacherOrThrow(teacherEmail, homeworkId);

        return new LinkedHashSet<>(request.getStudentIds())
                .stream()
                .map(this::findStudentOrThrow)
                .map(student -> findOrCreateAssignment(homework, student))
                .map(this::toAssignmentDTO)
                .toList();
    }

    /** Returns assignments for a teacher-owned homework task. */
    @Transactional(readOnly = true)
    public List<HomeworkAssignmentDTO> getAssignmentsForHomework(String teacherEmail, Long homeworkId) {
        findHomeworkForTeacherOrThrow(teacherEmail, homeworkId);
        return assignmentRepository.findByHomeworkIdAndHomeworkTeacherEmail(homeworkId, teacherEmail)
                .stream()
                .map(this::toAssignmentDTO)
                .toList();
    }

    /** Returns homework assigned to the authenticated student. */
    @Transactional(readOnly = true)
    public List<HomeworkAssignmentDTO> getStudentAssignments(String studentEmail) {
        return assignmentRepository.findByStudentEmail(studentEmail)
                .stream()
                .map(this::toAssignmentDTO)
                .toList();
    }

    /** Marks one student assignment complete after verifying the required work is done. */
    @Transactional
    public HomeworkAssignmentDTO completeAssignment(String studentEmail, Long assignmentId) {
        HomeworkAssignment assignment = assignmentRepository.findByIdAndStudentEmail(assignmentId, studentEmail)
                .orElseThrow(() -> new EntityNotFoundException("Homework assignment not found with id: " + assignmentId));

        if (assignment.getCompletedAt() != null) {
            return toAssignmentDTO(assignment);
        }

        CompletionState completionState = getCompletionState(assignment);

        if (!completionState.requirementsSatisfied()) {
            throw new IllegalArgumentException("Homework requirements must be completed before marking assignment complete");
        }

        assignment.setCompletedAt(LocalDateTime.now());
        return toAssignmentDTO(assignmentRepository.save(assignment));
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /** Copies request fields and validates that the selected work can actually be completed. */
    private void applyRequest(String teacherEmail, Homework homework, HomeworkRequest request) {
        validateRequirementFlags(request);
        Landmark landmark = findLandmarkForTeacherOrThrow(teacherEmail, request.getLandmarkId());

        if (Boolean.TRUE.equals(request.getRequireQuiz()) && !quizRepository.existsByLandmarkId(landmark.getId())) {
            throw new IllegalArgumentException("Homework that requires a quiz must use a landmark with a quiz");
        }

        homework.setTitle(request.getTitle());
        homework.setDescription(request.getDescription());
        homework.setDueDate(request.getDueDate());
        homework.setLandmark(landmark);
        homework.setRequireVisit(request.getRequireVisit());
        homework.setRequireQuiz(request.getRequireQuiz());
    }

    /** At least one requirement must be selected; otherwise the homework is already complete. */
    private void validateRequirementFlags(HomeworkRequest request) {
        if (!Boolean.TRUE.equals(request.getRequireVisit()) && !Boolean.TRUE.equals(request.getRequireQuiz())) {
            throw new IllegalArgumentException("Homework must require a landmark visit, a quiz, or both");
        }
    }

    /** Keeps assignment creation idempotent for repeated student IDs or already-assigned students. */
    private HomeworkAssignment findOrCreateAssignment(Homework homework, Student student) {
        return assignmentRepository.findByHomeworkIdAndStudentId(homework.getId(), student.getId())
                .orElseGet(() -> createAssignment(homework, student));
    }

    /** Creates the assignment row after duplicate checks have already run. */
    private HomeworkAssignment createAssignment(Homework homework, Student student) {
        HomeworkAssignment assignment = new HomeworkAssignment();
        assignment.setHomework(homework);
        assignment.setStudent(student);
        return assignmentRepository.save(assignment);
    }

    /** Calculates the current completion state from existing visit and quiz records. */
    private HomeworkAssignmentDTO toAssignmentDTO(HomeworkAssignment assignment) {
        CompletionState completionState = getCompletionState(assignment);
        return mapper.toAssignmentDTO(assignment, completionState.visitCompleted(), completionState.quizCompleted());
    }

    /** Checks visits and quiz completions against the assignment due date. */
    private CompletionState getCompletionState(HomeworkAssignment assignment) {
        Homework homework = assignment.getHomework();
        Long studentId = assignment.getStudent().getId();
        Long landmarkId = homework.getLandmark().getId();
        LocalDateTime deadline = homework.getDueDate().atTime(23, 59, 59);

        boolean visitCompleted = landmarkVisitRepository.existsByUserIdAndLandmarkIdAndDateVisitedLessThanEqual(
                studentId,
                landmarkId,
                deadline);
        boolean quizCompleted = homework.getLandmark().getQuiz() != null
                && quizCompletionRepository.existsByStudentIdAndQuizIdAndCompletedAtLessThanEqual(
                studentId,
                homework.getLandmark().getQuiz().getId(),
                deadline);

        boolean requirementsSatisfied = (!homework.getRequireVisit() || visitCompleted)
                && (!homework.getRequireQuiz() || quizCompleted);
        return new CompletionState(visitCompleted, quizCompleted, requirementsSatisfied);
    }

    /** Centralizes teacher ownership checks for homework management. */
    private Homework findHomeworkForTeacherOrThrow(String teacherEmail, Long id) {
        return homeworkRepository.findByIdAndTeacherEmail(id, teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("Homework not found with id: " + id));
    }

    /** Reuses landmark ownership as the authorization boundary for homework. */
    private Landmark findLandmarkForTeacherOrThrow(String teacherEmail, Long landmarkId) {
        return landmarkRepository.findByIdAndOwnerEmail(landmarkId, teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("Landmark not found with id: " + landmarkId));
    }

    private Teacher findTeacherOrThrow(String teacherEmail) {
        User user = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + teacherEmail));

        if (user instanceof Teacher teacher) {
            return teacher;
        }

        throw new IllegalArgumentException("Only teachers can manage homework");
    }

    private Student findStudentOrThrow(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + studentId));

        if (user instanceof Student student) {
            return student;
        }

        throw new IllegalArgumentException("Homework can only be assigned to students");
    }

    /** Small value object that keeps the three completion flags together. */
    private record CompletionState(boolean visitCompleted, boolean quizCompleted, boolean requirementsSatisfied) {
    }
}
