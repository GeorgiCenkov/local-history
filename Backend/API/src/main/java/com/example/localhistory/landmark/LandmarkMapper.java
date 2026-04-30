package com.example.localhistory.landmark;

import com.example.localhistory.landmark.dto.response.LandmarkDTO;
import com.example.localhistory.landmark.dto.response.LandmarkVisitDTO;
import com.example.localhistory.landmark.model.Landmark;
import com.example.localhistory.landmark.model.LandmarkVisit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LandmarkMapper {

    // visitsCount is derived from the size of the visits collection, not a
    // direct field mapping, so we compute it in a default method instead.
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "visitsCount", expression = "java(landmark.getVisits() != null ? landmark.getVisits().size() : 0)")
    @Mapping(target = "quizId", expression = "java(landmark.getQuiz() != null ? landmark.getQuiz().getId() : null)")
    LandmarkDTO toDTO(Landmark landmark);

    // userId and landmarkId come from nested objects, so we tell MapStruct
    // exactly where to find them with dot-notation source paths.
    @Mapping(source = "user.id",     target = "userId")
    @Mapping(source = "landmark.id", target = "landmarkId")
    LandmarkVisitDTO toVisitDTO(LandmarkVisit visit);
}
