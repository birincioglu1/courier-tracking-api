package com.birincioglu.couriertrackingapi.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courier")
public class Courier extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "username", nullable = false, updatable = false, unique = true, length = 20)
    private String username;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CourierLocation> locationHistory = new ArrayList<>();

    @Column(name = "total_distance", nullable = false)
    @Builder.Default
    private double totalDistance = 0.0;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    public void addLocation(CourierLocation location) {
        if (!locationHistory.isEmpty()) {
            double distance = locationHistory.get(locationHistory.size() - 1).getGeoLocation().distanceTo(location.getGeoLocation());
            this.totalDistance = totalDistance + distance;
        }
        location.setCourier(this);
        locationHistory.add(location);
    }
}

