package com.birincioglu.couriertrackingapi.presentation.controller;

import com.birincioglu.couriertrackingapi.application.StoreService;
import com.birincioglu.couriertrackingapi.domain.model.dto.PageableDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.StoreDTO;
import com.birincioglu.couriertrackingapi.domain.model.query.PageableQuery;
import com.birincioglu.couriertrackingapi.presentation.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Store Operations", description = "API for managing store-related operations")
public class StoreController {

    private final StoreService service;

    @Operation(summary = "List All Stores", description = "Lists all stores in the system with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stores successfully listed"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping
    public Response<PageableDTO<StoreDTO>> getAllStores(
            @Parameter(description = "Pagination parameters") @Valid PageableQuery query) {
        log.info("[START] Get travel total count request handled");
        return Response.<PageableDTO<StoreDTO>>builder().data(service.getStores(query)).build();
    }

}

