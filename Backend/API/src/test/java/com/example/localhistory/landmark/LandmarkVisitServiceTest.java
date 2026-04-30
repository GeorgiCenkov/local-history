package com.example.localhistory.landmark;

import com.example.localhistory.coordinates.Coordinates;
import com.example.localhistory.landmark.dto.request.LandmarkVisitRequest;
import com.example.localhistory.landmark.dto.response.LandmarkVisitDTO;
import com.example.localhistory.landmark.model.Landmark;
import com.example.localhistory.landmark.model.LandmarkVisit;
import com.example.localhistory.user.UserRepository;
import com.example.localhistory.user.UserService;
import com.example.localhistory.user.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LandmarkVisitServiceTest {

    @Mock
    private LandmarkRepository landmarkRepository;

    @Mock
    private LandmarkVisitRepository landmarkVisitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private LandmarkMapper mapper;

    @InjectMocks
    private LandmarkVisitService service;

    @Test
    void submitVisitAcceptsCoordinatesWithinFiftyMeters() {
        Student student = new Student();
        student.setId(10L);
        Landmark landmark = landmarkAt(1L, 42.697708, 23.321868);
        LandmarkVisitDTO dto = new LandmarkVisitDTO();
        LandmarkVisitRequest request = requestAt(1L, 42.697800, 23.321950);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(landmarkRepository.findById(1L)).thenReturn(Optional.of(landmark));
        when(landmarkVisitRepository.save(any(LandmarkVisit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toVisitDTO(any(LandmarkVisit.class))).thenReturn(dto);

        LandmarkVisitDTO result = service.submitVisit("student@example.com", request);

        assertThat(result).isSameAs(dto);
        verify(userService).awardPoints(10L, 25);

        ArgumentCaptor<LandmarkVisit> visitCaptor = ArgumentCaptor.forClass(LandmarkVisit.class);
        verify(landmarkVisitRepository).save(visitCaptor.capture());
        assertThat(visitCaptor.getValue().getCoordinates()).isSameAs(request.getCoordinates());
    }

    @Test
    void submitVisitRejectsCoordinatesFartherThanFiftyMeters() {
        Student student = new Student();
        Landmark landmark = landmarkAt(1L, 42.697708, 23.321868);
        LandmarkVisitRequest request = requestAt(1L, 42.700000, 23.321868);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(landmarkRepository.findById(1L)).thenReturn(Optional.of(landmark));

        assertThatThrownBy(() -> service.submitVisit("student@example.com", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Visit must be submitted within 50 meters of the landmark");

        verify(userService, never()).awardPoints(anyLong(), anyInt());
        verify(landmarkVisitRepository, never()).save(any());
    }

    private Landmark landmarkAt(Long id, double latitude, double longitude) {
        Landmark landmark = new Landmark();
        landmark.setId(id);
        landmark.setCoordinates(coordinates(latitude, longitude));
        landmark.setVisitRewardPoints(25);
        return landmark;
    }

    private LandmarkVisitRequest requestAt(Long landmarkId, double latitude, double longitude) {
        LandmarkVisitRequest request = new LandmarkVisitRequest();
        request.setLandmarkId(landmarkId);
        request.setImage("proof.jpg");
        request.setCoordinates(coordinates(latitude, longitude));
        return request;
    }

    private Coordinates coordinates(double latitude, double longitude) {
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(latitude);
        coordinates.setLongitude(longitude);
        return coordinates;
    }
}
