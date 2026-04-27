package com.example.localhistory.landmark.dto.response;

import com.example.localhistory.coordinates.Coordinates;
import java.time.LocalDateTime;

public class LandmarkVisitDTO {

    private Long id;

    private Long userId;

    private Long landmarkId;

    private LocalDateTime dateVisited;

    private String image;

    private Coordinates coordinates;

    public LandmarkVisitDTO() {}

    public LandmarkVisitDTO(Long id,
                                 Long userId,
                                 Long landmarkId,
                                 LocalDateTime dateVisited,
                                 String image,
                                 Coordinates coordinates) {
        this.id = id;
        this.userId = userId;
        this.landmarkId = landmarkId;
        this.dateVisited = dateVisited;
        this.image = image;
        this.coordinates = coordinates;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getLandmarkId() {
        return landmarkId;
    }

    public LocalDateTime getDateVisited() {
        return dateVisited;
    }

    public String getImage() {
        return image;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}