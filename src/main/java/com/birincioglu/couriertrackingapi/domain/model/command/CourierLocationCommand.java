package com.birincioglu.couriertrackingapi.domain.model.command;

import com.birincioglu.couriertrackingapi.domain.model.dto.GeoLocationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Command object for updating courier location")
public class CourierLocationCommand {

    @NotNull
    @Schema(description = "Geographic location coordinates of the courier")
    private GeoLocationDTO geoLocation;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?$", message = "Invalid date format. Exp: 2025-04-04T12:30")
    @Schema(description = "Timestamp of the location update", example = "2024-04-05T14:30", pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?$")
    private String time;
}

