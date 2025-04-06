
package com.birincioglu.couriertrackingapi.domain.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadStoreVO {

    private String name;
    private double lat;
    private double lng;


}

