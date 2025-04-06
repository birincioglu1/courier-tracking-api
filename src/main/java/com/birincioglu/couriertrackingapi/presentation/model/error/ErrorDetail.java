package com.birincioglu.couriertrackingapi.presentation.model.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetail {
    private int code;
    private String type;
    private String title;
}
