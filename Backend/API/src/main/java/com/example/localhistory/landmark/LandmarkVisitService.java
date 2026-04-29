package com.example.localhistory.landmark;

import com.example.localhistory.landmark.dto.request.LandmarkVisitRequest;
import com.example.localhistory.landmark.dto.response.LandmarkVisitDTO;
import com.example.localhistory.landmark.model.Landmark;
import com.example.localhistory.landmark.model.LandmarkVisit;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LandmarkVisitService {

    private final LandmarkRepository landmarkRepository;
    private final LandmarkVisitRepository landmarkVisitRepository;
    private final UserRepository userRepository;
    private final LandmarkMapper mapper;

    //TODO: add validation that the request is coming from a place near enough
    @Transactional
    public LandmarkVisitDTO submitVisit(String userEmail, LandmarkVisitRequest request) {
        Student student = findStudentOrThrow(userEmail);
        Landmark landmark = landmarkRepository.findById(request.getLandmarkId())
                .orElseThrow(() -> new EntityNotFoundException("Landmark not found with id: " + request.getLandmarkId()));

        LandmarkVisit visit = new LandmarkVisit();
        visit.setUser(student);
        visit.setLandmark(landmark);
        visit.setImage(request.getImage());
        visit.setCoordinates(request.getCoordinates());

        return mapper.toVisitDTO(landmarkVisitRepository.save(visit));
    }

    @Transactional
    public void deleteVisit(Long visitId) {
        if (!landmarkVisitRepository.existsById(visitId)) {
            throw new EntityNotFoundException("Landmark visit not found with id: " + visitId);
        }
        landmarkVisitRepository.deleteById(visitId);
    }

    private Student findStudentOrThrow(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));

        if (user instanceof Student student) {
            return student;
        }

        throw new IllegalArgumentException("Only students can submit landmark visits");
    }
}
