package com.birincioglu.couriertrackingapi.presentation.model.response;

import com.birincioglu.couriertrackingapi.presentation.model.error.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private T data;
    private ErrorResponse error;
}
