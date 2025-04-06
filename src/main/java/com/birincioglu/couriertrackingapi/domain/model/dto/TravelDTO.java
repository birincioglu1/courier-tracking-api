package com.birincioglu.couriertrackingapi.domain.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelDTO {
    private Double totalDistance;
    private String type;
}
