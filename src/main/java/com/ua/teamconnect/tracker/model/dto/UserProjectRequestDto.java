package com.ua.teamconnect.tracker.model.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "User Project", description = "Project IDs for the user")
public record UserProjectRequestDto(
                
          @Schema(
             description = "List of project IDs",
             example = "[1, 2, 3]")
          @NotEmpty
          List<@NotNull Integer> projectIds
                
    ) {
}
