package com.birincioglu.couriertrackingapi.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeoLocationTest {

    @Test
    void testDistanceTo_SameLocation() {
        GeoLocation location1 = GeoLocation.builder()
                .lat(41.0082)
                .lng(28.9784)
                .build();

        GeoLocation location2 = GeoLocation.builder()
                .lat(41.0082)
                .lng(28.9784)
                .build();

        double distance = location1.distanceTo(location2);
        assertEquals(0.0, distance, 0.1);
    }

    @Test
    void testDistanceTo_DifferentLocations() {
        GeoLocation location1 = GeoLocation.builder()
                .lat(41.0082)
                .lng(28.9784)
                .build();

        GeoLocation location2 = GeoLocation.builder()
                .lat(41.0083)
                .lng(28.9785)
                .build();

        double distance = location1.distanceTo(location2);
        assertTrue(distance > 0);
    }

    @Test
    void testDistanceTo_OppositeLocations() {
        GeoLocation location1 = GeoLocation.builder()
                .lat(41.0082)
                .lng(28.9784)
                .build();

        GeoLocation location2 = GeoLocation.builder()
                .lat(-41.0082)
                .lng(-28.9784)
                .build();

        double distance = location1.distanceTo(location2);
        assertTrue(distance > 0);
    }
} 