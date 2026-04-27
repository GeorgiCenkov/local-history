package com.example.localhistory.coordinates;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


// Used to store coordinates and embed it where needed
@Embeddable
public class Coordinates {
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    protected Coordinates() {}

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}