package com.birincioglu.couriertrackingapi.domain.model.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class PageableQuery {
    @Min(value = 1, message = "validation.min-page-error")
    @Builder.Default
    private int page = 1;

    @Min(value = 1, message = "validation.min-page-size-error")
    @Max(value = 50, message = "validation.max-page-size-error")
    @Builder.Default
    private int size = 5;
}
