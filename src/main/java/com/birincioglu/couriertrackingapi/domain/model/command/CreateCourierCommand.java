package com.birincioglu.couriertrackingapi.domain.model.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Command object for creating a new courier")
public class CreateCourierCommand {

    @NotNull
    @Length(min = 3, max = 20)
    @Schema(description = "Unique username for the courier", example = "marc_marquez", minLength = 3, maxLength = 20)
    private String username;

}

