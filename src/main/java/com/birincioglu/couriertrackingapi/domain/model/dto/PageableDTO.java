package com.birincioglu.couriertrackingapi.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class PageableDTO<T> {

    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    private Collection<T> content;
}
