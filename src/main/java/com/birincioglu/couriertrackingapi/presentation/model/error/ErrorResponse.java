package com.birincioglu.couriertrackingapi.presentation.model.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    @Builder.Default
    private List<ErrorDetail> errors = new ArrayList<>();
}
