package com.birincioglu.couriertrackingapi.presentation.controller;

import com.birincioglu.couriertrackingapi.application.CourierService;
import com.birincioglu.couriertrackingapi.domain.model.command.CourierLocationCommand;
import com.birincioglu.couriertrackingapi.domain.model.command.CreateCourierCommand;
import com.birincioglu.couriertrackingapi.domain.model.dto.CourierDTO;
import com.birincioglu.couriertrackingapi.domain.model.dto.TravelDTO;
import com.birincioglu.couriertrackingapi.presentation.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courier")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Courier Operations", description = "API for managing courier-related operations")
public class CourierController {

    private final CourierService service;

    @Operation(summary = "Create New Courier", description = "Creates a new courier record in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Courier successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CourierDTO> save(@Valid @RequestBody CreateCourierCommand command) {
        log.info("[START] Create courier request handled. command: {}", command);
        return Response.<CourierDTO>builder().data(service.save(command)).build();
    }

    @Operation(summary = "Update Courier Location", description = "Updates the location information of the specified courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Courier not found")
    })
    @PutMapping("/{courierId}/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upsertCourierLocation(
            @Parameter(description = "Courier ID") @PathVariable Long courierId,
            @Valid @RequestBody CourierLocationCommand command) {
        log.info("[START] Upsert courier location request handled: courierId:{}, command: {}", courierId, command);
        service.upsertCourierLocation(courierId, command);
    }

    @Operation(summary = "Get Total Travel Distance", description = "Calculates the total distance traveled by the specified courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total distance successfully calculated"),
            @ApiResponse(responseCode = "404", description = "Courier not found")
    })
    @GetMapping("/{courierId}")
    public Response<TravelDTO> getTotalTravelDistance(
            @Parameter(description = "Courier ID") @PathVariable Long courierId) {
        log.info("[START] Retrieve total distance request handled: courierId:{}", courierId);
        return Response.<TravelDTO>builder().data(service.getTotalTravelDistance(courierId)).build();
    }
}

