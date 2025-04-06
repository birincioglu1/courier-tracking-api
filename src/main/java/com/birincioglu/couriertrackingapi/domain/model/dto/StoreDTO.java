package com.birincioglu.couriertrackingapi.domain.model.dto;


import com.birincioglu.couriertrackingapi.domain.entity.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreDTO {
    private String id;
    private String name;
    private GeoLocation location;
}
