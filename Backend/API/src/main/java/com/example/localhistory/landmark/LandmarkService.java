package com.example.localhistory.landmark;

import com.example.localhistory.landmark.dto.request.LandmarkRequest;
import com.example.localhistory.landmark.dto.response.LandmarkDTO;
import com.example.localhistory.landmark.dto.response.LandmarkVisitDTO;
import com.example.localhistory.landmark.model.Landmark;
import com.example.localhistory.landmark.model.LandmarkVisit;
import com.example.localhistory.homework.HomeworkAssignmentRepository;
import com.example.localhistory.homework.HomeworkRepository;
import com.example.localhistory.quiz.QuizCompletionRepository;
import com.example.localhistory.quiz.QuizRepository;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.model.Teacher;
import com.example.localhistory.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final LandmarkVisitRepository landmarkVisitRepository;
    private final QuizCompletionRepository quizCompletionRepository;
    private final QuizRepository quizRepository;
    private final HomeworkAssignmentRepository homeworkAssignmentRepository;
    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;
    private final LandmarkMapper mapper;

    /** Returns every landmark; read-only transaction avoids unnecessary locking. */
    @Transactional(readOnly = true)
    public List<LandmarkDTO> getAllTeacherLandmarks(String teacherEmail) {
        return landmarkRepository.findByOwnerEmail(teacherEmail)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /** Returns every landmark; read-only transaction avoids unnecessary locking. */
    @Transactional(readOnly = true)
    public List<LandmarkDTO> getAllLandmarks() {
        return landmarkRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /** Fetches a single landmark by ID, throwing 404 if it doesn't exist. */
    @Transactional(readOnly = true)
    public LandmarkDTO getLandmarkById(Long id) {
        return mapper.toDTO(findLandmarkOrThrow(id));
    }

    /** Persists a brand-new landmark built from the validated request body. */
    @Transactional
    public LandmarkDTO createLandmark(String teacherEmail, LandmarkRequest request) {
        Landmark landmark = new Landmark();
        landmark.setOwner(findTeacherOrThrow(teacherEmail));
        applyRequest(landmark, request);
        return mapper.toDTO(landmarkRepository.save(landmark));
    }

    /**
     * Fully replaces all mutable fields on an existing landmark (PUT semantics).
     * Every field in the request overwrites what is currently stored.
     */
    @Transactional
    public LandmarkDTO updateLandmark(String teacherEmail, Long id, LandmarkRequest request) {
        Landmark landmark = findLandmarkForTeacherOrThrow(teacherEmail, id);
        applyRequest(landmark, request);
        return mapper.toDTO(landmarkRepository.save(landmark));
    }

    /** Permanently removes a landmark. Throws 404 if the ID is unknown. */
    @Transactional
    public void deleteLandmark(String teacherEmail, Long id) {
        Landmark landmark = findLandmarkForTeacherOrThrow(teacherEmail, id);
        Long landmarkId = landmark.getId();

        // Dependent rows hold foreign keys to the landmark or its quiz, so remove
        // them from the leaves inward before deleting the landmark row.
        landmarkVisitRepository.deleteByLandmarkId(landmarkId);
        homeworkAssignmentRepository.deleteByHomeworkLandmarkId(landmarkId);
        homeworkRepository.deleteByLandmarkId(landmarkId);
        quizCompletionRepository.deleteByQuizLandmarkId(landmarkId);
        quizRepository.deleteByLandmarkId(landmarkId);
        landmarkRepository.delete(landmark);
    }

    /** Returns all visits recorded against a specific landmark. */
    @Transactional(readOnly = true)
    public List<LandmarkVisitDTO> getVisitsForLandmark(Long landmarkId) {
        findLandmarkOrThrow(landmarkId);
        return landmarkVisitRepository.findByLandmarkId(landmarkId)
                .stream()
                .map(mapper::toVisitDTO)
                .toList();
    }

    /** Fetches a single visit record, throwing 404 if not found. */
    @Transactional(readOnly = true)
    public LandmarkVisitDTO getVisitById(Long visitId) {
        LandmarkVisit visit = landmarkVisitRepository.findById(visitId)
                .orElseThrow(() -> new EntityNotFoundException("Landmark visit not found with id: " + visitId));
        return mapper.toVisitDTO(visit);
    }

    /** Removes a visit record — useful for moderation by teachers. */
    @Transactional
    public void deleteVisit(String teacherEmail, Long visitId) {
        LandmarkVisit visit = landmarkVisitRepository.findByIdAndLandmarkOwnerEmail(visitId, teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("Landmark visit not found with id: " + visitId));
        landmarkVisitRepository.delete(visit);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /** Copies all fields from a validated request onto the given entity. */
    private void applyRequest(Landmark landmark, LandmarkRequest request) {
        landmark.setTitle(request.getTitle());
        landmark.setDescription(request.getDescription());
        landmark.setImageUrl(request.getImageUrl());
        landmark.setCoordinates(request.getCoordinates());
        landmark.setVisitRewardPoints(request.getVisitRewardPoints());
    }

    /** Centralizes the "find or 404" pattern used across several methods. */
    private Landmark findLandmarkForTeacherOrThrow(String teacherEmail, Long id) {
        return landmarkRepository.findByIdAndOwnerEmail(id, teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("Landmark not found with id: " + id));
    }

    /** Centralizes the "find or 404" pattern used across several methods. */
    private Landmark findLandmarkOrThrow(Long id) {
        return landmarkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Landmark not found with id: " + id));
    }

    private Teacher findTeacherOrThrow(String teacherEmail) {
        User user = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + teacherEmail));

        if (user instanceof Teacher teacher) {
            return teacher;
        }

        throw new IllegalArgumentException("Only teachers can manage landmarks");
    }

}
