package com.example.localhistory.quiz;

import com.example.localhistory.quiz.dto.response.PublicQuizDTO;
import com.example.localhistory.quiz.dto.response.PublicQuizQuestionDTO;
import com.example.localhistory.quiz.dto.response.QuizDTO;
import com.example.localhistory.quiz.dto.response.QuizQuestionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    // landmarkId comes from the associated Landmark entity rather than a direct
    // field on Quiz, so MapStruct needs the nested source path.
    @Mapping(source = "landmark.id", target = "landmarkId")
    QuizDTO toDTO(Quiz quiz);

    QuizQuestionDTO toQuestionDTO(QuizQuestion question);

    // Public quiz reads intentionally omit correctAnswer so students cannot
    // fetch answers before submitting.
    @Mapping(source = "landmark.id", target = "landmarkId")
    PublicQuizDTO toPublicDTO(Quiz quiz);

    PublicQuizQuestionDTO toPublicQuestionDTO(QuizQuestion question);
}
