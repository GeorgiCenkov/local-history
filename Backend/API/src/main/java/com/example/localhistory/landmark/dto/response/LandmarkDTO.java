package com.example.localhistory.landmark.dto.response;

import com.example.localhistory.coordinates.Coordinates;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LandmarkDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateCreated;
    private String imageUrl;
    private Coordinates coordinates;
    private Long ownerId;
    private Integer visitRewardPoints;
    private Integer visitsCount;
    private Long quizId;
}
