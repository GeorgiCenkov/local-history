package com.example.localhistory.landmark;


import com.example.localhistory.landmark.dto.request.LandmarkRequest;
import com.example.localhistory.landmark.dto.response.LandmarkDTO;
import com.example.localhistory.landmark.dto.response.LandmarkVisitDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for landmark management.
 * All endpoints are restricted to users with the TEACHER role — students can
 * view landmarks through their own dedicated read endpoints elsewhere.
 */
@RestController
@RequestMapping("/api/landmarks")
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;

    /**
     * GET /api/landmarks — list all landmarks with visit counts.
     */
    @GetMapping
    public ResponseEntity<List<LandmarkDTO>> getAllLandmarks(Authentication authentication) {
        return ResponseEntity.ok(landmarkService.getAllLandmarks());
    }

    /**
     * GET /api/landmarks/teacher — list all landmarks with visit counts for a teacher.
     */
    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<LandmarkDTO>> getAllTeacherLandmarks(Authentication authentication) {
        return ResponseEntity.ok(landmarkService.getAllTeacherLandmarks(authentication.getName()));
    }

    /**
     * GET /api/landmarks/{id} — fetch a single landmark by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LandmarkDTO> getLandmarkById(
            @PathVariable Long id) {
        return ResponseEntity.ok(landmarkService.getLandmarkById(id));
    }

    /**
     * POST /api/landmarks — create a new landmark.
     * Returns 201 Created with the persisted landmark in the body.
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<LandmarkDTO> createLandmark(
            @Valid @RequestBody LandmarkRequest request,
            Authentication authentication) {
        LandmarkDTO created = landmarkService.createLandmark(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/landmarks/{id} — replace all fields on an existing landmark.
     * The full request body is required; partial updates are not supported here.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<LandmarkDTO> updateLandmark(
            @PathVariable Long id,
            @Valid @RequestBody LandmarkRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(landmarkService.updateLandmark(authentication.getName(), id, request));
    }

    /**
     * DELETE /api/landmarks/{id} — permanently remove a landmark.
     * Returns 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteLandmark(
            @PathVariable Long id,
            Authentication authentication) {
        landmarkService.deleteLandmark(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Landmark visits ──────────────────────────────────────────────────────

    /**
     * GET /api/landmarks/{id}/visits — list all visits for a landmark.
     */
    @GetMapping("/{id}/visits")
    public ResponseEntity<List<LandmarkVisitDTO>> getVisitsForLandmark(
            @PathVariable Long id) {
        return ResponseEntity.ok(landmarkService.getVisitsForLandmark(id));
    }

    /**
     * GET /api/landmarks/visits/{visitId} — fetch a single visit record.
     */
    @GetMapping("/visits/{visitId}")
    public ResponseEntity<LandmarkVisitDTO> getVisitById(
            @PathVariable Long visitId) {
        return ResponseEntity.ok(landmarkService.getVisitById(visitId));
    }

    /**
     * DELETE /api/landmarks/visits/{visitId} — remove a visit record.
     * Useful for moderation (e.g. rejecting fraudulent proof images).
     */
    @DeleteMapping("/visits/{visitId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteVisit(
            @PathVariable Long visitId,
            Authentication authentication) {
        landmarkService.deleteVisit(authentication.getName(), visitId);
        return ResponseEntity.noContent().build();
    }
}
