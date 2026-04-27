package com.example.localhistory.coordinates;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;


// Used to store coordinates and embed it where needed
@Embeddable
@Data
public class Coordinates {
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;
}