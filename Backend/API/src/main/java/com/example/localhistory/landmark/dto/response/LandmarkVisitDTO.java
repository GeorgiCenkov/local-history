package com.example.localhistory.landmark.dto.response;

import com.example.localhistory.coordinates.Coordinates;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LandmarkVisitDTO {

    private Long id;

    private Long userId;

    private Long landmarkId;

    private LocalDateTime dateVisited;

    private String image;

    private Coordinates coordinates;
}