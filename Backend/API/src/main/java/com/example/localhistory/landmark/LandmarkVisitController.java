package com.example.localhistory.landmark;

import com.example.localhistory.landmark.dto.request.LandmarkVisitRequest;
import com.example.localhistory.landmark.dto.response.LandmarkVisitDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/landmark-visits")
@RequiredArgsConstructor
public class LandmarkVisitController {

    private final LandmarkVisitService landmarkVisitService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LandmarkVisitDTO> submitVisit(
            @Valid @RequestBody LandmarkVisitRequest request,
            Authentication authentication) {
        LandmarkVisitDTO created = landmarkVisitService.submitVisit(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{visitId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteVisit(@PathVariable Long visitId) {
        landmarkVisitService.deleteVisit(visitId);
        return ResponseEntity.noContent().build();
    }
}
