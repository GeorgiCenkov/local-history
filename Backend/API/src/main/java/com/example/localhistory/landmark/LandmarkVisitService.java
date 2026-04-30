package com.example.localhistory.landmark;

import com.example.localhistory.coordinates.Coordinates;
import com.example.localhistory.landmark.dto.request.LandmarkVisitRequest;
import com.example.localhistory.landmark.dto.response.LandmarkVisitDTO;
import com.example.localhistory.landmark.model.Landmark;
import com.example.localhistory.landmark.model.LandmarkVisit;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.UserService;
import com.example.localhistory.user.model.Student;
import com.example.localhistory.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LandmarkVisitService {

    private static final double MAX_VISIT_DISTANCE_METERS = 50.0;
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private final LandmarkRepository landmarkRepository;
    private final LandmarkVisitRepository landmarkVisitRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LandmarkMapper mapper;

    @Transactional
    public LandmarkVisitDTO submitVisit(String userEmail, LandmarkVisitRequest request) {
        Student student = findStudentOrThrow(userEmail);
        Landmark landmark = landmarkRepository.findById(request.getLandmarkId())
                .orElseThrow(() -> new EntityNotFoundException("Landmark not found with id: " + request.getLandmarkId()));

        validateVisitIsNearLandmark(request.getCoordinates(), landmark.getCoordinates());

        LandmarkVisit visit = new LandmarkVisit();
        visit.setUser(student);
        visit.setLandmark(landmark);
        visit.setImage(request.getImage());
        visit.setCoordinates(request.getCoordinates());

        // Award points to user
        userService.awardPoints(student.getId(), landmark.getVisitRewardPoints());

        return mapper.toVisitDTO(landmarkVisitRepository.save(visit));
    }

    @Transactional
    public void deleteVisit(String teacherEmail, Long visitId) {
        LandmarkVisit visit = landmarkVisitRepository.findByIdAndLandmarkOwnerEmail(visitId, teacherEmail)
                .orElseThrow(() -> new EntityNotFoundException("Landmark visit not found with id: " + visitId));
        landmarkVisitRepository.delete(visit);
    }

    private Student findStudentOrThrow(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));

        if (user instanceof Student student) {
            return student;
        }

        throw new IllegalArgumentException("Only students can submit landmark visits");
    }

    private void validateVisitIsNearLandmark(Coordinates visitCoordinates, Coordinates landmarkCoordinates) {
        if (visitCoordinates == null || landmarkCoordinates == null) {
            throw new IllegalArgumentException("Visit and landmark coordinates are required");
        }

        double distanceMeters = calculateDistanceMeters(visitCoordinates, landmarkCoordinates);

        if (distanceMeters > MAX_VISIT_DISTANCE_METERS) {
            throw new IllegalArgumentException("Visit must be submitted within 50 meters of the landmark");
        }
    }

    private double calculateDistanceMeters(Coordinates first, Coordinates second) {
        double firstLatitude = Math.toRadians(first.getLatitude());
        double secondLatitude = Math.toRadians(second.getLatitude());
        double latitudeDelta = Math.toRadians(second.getLatitude() - first.getLatitude());
        double longitudeDelta = Math.toRadians(second.getLongitude() - first.getLongitude());

        double haversine = Math.sin(latitudeDelta / 2) * Math.sin(latitudeDelta / 2)
                + Math.cos(firstLatitude) * Math.cos(secondLatitude)
                * Math.sin(longitudeDelta / 2) * Math.sin(longitudeDelta / 2);
        double angularDistance = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

        return EARTH_RADIUS_METERS * angularDistance;
    }
}
