package com.example.localhistory.homework;

import com.example.localhistory.homework.dto.request.HomeworkAssignmentRequest;
import com.example.localhistory.homework.dto.request.HomeworkRequest;
import com.example.localhistory.homework.dto.response.HomeworkAssignmentDTO;
import com.example.localhistory.homework.dto.response.HomeworkDTO;
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

// REST controller for homework CRUD, bulk assignment, and student assignment reads.
@RestController
@RequestMapping("/api/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    /**
     * GET /api/homework/teacher — list homework created by the authenticated teacher.
     */
    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<HomeworkDTO>> getTeacherHomework(Authentication authentication) {
        return ResponseEntity.ok(homeworkService.getTeacherHomework(authentication.getName()));
    }

    /**
     * GET /api/homework/{id}/teacher — fetch one teacher-owned homework task.
     */
    @GetMapping("/{id}/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<HomeworkDTO> getTeacherHomeworkById(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(homeworkService.getTeacherHomeworkById(authentication.getName(), id));
    }

    /**
     * POST /api/homework — create a homework task for a teacher-owned landmark.
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<HomeworkDTO> createHomework(
            @Valid @RequestBody HomeworkRequest request,
            Authentication authentication) {
        HomeworkDTO created = homeworkService.createHomework(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/homework/{id} — replace all mutable fields on a teacher-owned homework task.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<HomeworkDTO> updateHomework(
            @PathVariable Long id,
            @Valid @RequestBody HomeworkRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(homeworkService.updateHomework(authentication.getName(), id, request));
    }

    /**
     * DELETE /api/homework/{id} — delete homework and its student assignments.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteHomework(
            @PathVariable Long id,
            Authentication authentication) {
        homeworkService.deleteHomework(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/homework/{id}/assignments — assign the same homework to multiple students.
     */
    @PostMapping("/{id}/assignments")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<HomeworkAssignmentDTO>> assignHomework(
            @PathVariable Long id,
            @Valid @RequestBody HomeworkAssignmentRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(homeworkService.assignHomework(authentication.getName(), id, request));
    }

    /**
     * GET /api/homework/{id}/assignments — list assignments for a teacher-owned homework task.
     */
    @GetMapping("/{id}/assignments")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<HomeworkAssignmentDTO>> getAssignmentsForHomework(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(homeworkService.getAssignmentsForHomework(authentication.getName(), id));
    }

    /**
     * GET /api/homework/student — list homework assigned to the authenticated student.
     */
    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<HomeworkAssignmentDTO>> getStudentAssignments(Authentication authentication) {
        return ResponseEntity.ok(homeworkService.getStudentAssignments(authentication.getName()));
    }

    /**
     * POST /api/homework/assignments/{id}/complete — mark one student assignment complete.
     */
    @PostMapping("/assignments/{id}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<HomeworkAssignmentDTO> completeAssignment(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(homeworkService.completeAssignment(authentication.getName(), id));
    }
}
