package com.example.localhistory.quiz;

import com.example.localhistory.quiz.dto.request.QuizRequest;
import com.example.localhistory.quiz.dto.request.QuizSubmissionRequest;
import com.example.localhistory.quiz.dto.response.PublicQuizDTO;
import com.example.localhistory.quiz.dto.response.QuizDTO;
import com.example.localhistory.quiz.dto.response.QuizSubmissionResultDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for quiz reads, teacher quiz management, and quiz submission.
 * Public/student reads never expose correct answers; teacher management endpoints
 * return the full quiz so teachers can edit answer keys.
 */
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /**
     * GET /api/quizzes — list all quizzes without correct answers.
     */
    @GetMapping
    public ResponseEntity<List<PublicQuizDTO>> getAllQuizzes(Authentication authentication) {
        return ResponseEntity.ok(quizService.getAllQuizzes(authentication.getName()));
    }

    /**
     * GET /api/quizzes/{id} — fetch one public quiz by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicQuizDTO> getQuizById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(quizService.getQuizById(authentication.getName(), id));
    }

    /**
     * GET /api/quizzes/landmark/{landmarkId} — fetch the quiz for a landmark.
     */
    @GetMapping("/landmark/{landmarkId}")
    public ResponseEntity<PublicQuizDTO> getQuizByLandmarkId(
            @PathVariable Long landmarkId,
            Authentication authentication) {
        return ResponseEntity.ok(quizService.getQuizByLandmarkId(authentication.getName(), landmarkId));
    }

    /**
     * GET /api/quizzes/teacher — list all quizzes for the authenticated teacher.
     */
    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<QuizDTO>> getTeacherQuizzes(Authentication authentication) {
        return ResponseEntity.ok(quizService.getTeacherQuizzes(authentication.getName()));
    }

    /**
     * GET /api/quizzes/{id}/teacher — fetch a teacher-owned quiz with answers.
     */
    @GetMapping("/{id}/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuizDTO> getTeacherQuizById(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(quizService.getTeacherQuizById(authentication.getName(), id));
    }

    /**
     * POST /api/quizzes — create a quiz for a teacher-owned landmark.
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuizDTO> createQuiz(
            @Valid @RequestBody QuizRequest request,
            Authentication authentication) {
        QuizDTO created = quizService.createQuiz(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/quizzes/{id} — replace all mutable fields on a teacher-owned quiz.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<QuizDTO> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(quizService.updateQuiz(authentication.getName(), id, request));
    }

    /**
     * DELETE /api/quizzes/{id} — permanently remove a teacher-owned quiz.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Long id,
            Authentication authentication) {
        quizService.deleteQuiz(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/quizzes/{id}/submit — grade a student's answers immediately.
     * The first completion is persisted so retakes can be graded without more XP.
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizSubmissionResultDTO> submitQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizSubmissionRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(quizService.submitQuiz(authentication.getName(), id, request));
    }
}
