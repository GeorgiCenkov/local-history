package com.example.localhistory.landmark.dto.request;

import com.example.localhistory.coordinates.Coordinates;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LandmarkVisitRequest {

    @NotNull(message = "Landmark ID is required")
    private Long landmarkId;

    @NotBlank(message = "Image is required")
    private String image;

    @NotNull(message = "Coordinates are required")
    @Valid
    private Coordinates coordinates;
}
