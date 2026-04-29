package com.example.localhistory.landmark.dto.request;

import com.example.localhistory.coordinates.Coordinates;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LandmarkRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 40, message = "Title must not exceed 40 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "Coordinates are required")
    @Valid
    private Coordinates coordinates;

    @NotNull(message = "Visit reward points are required")
    @Min(value = 0, message = "Reward points must be non-negative")
    private Integer visitRewardPoints;
}
