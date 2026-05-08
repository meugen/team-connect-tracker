package com.ua.teamconnect.tracker.model.annotation;

import com.ua.teamconnect.tracker.model.dto.ErrorDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "404",
    description = "Resource with provided ID is not found",
    content = @Content(schema = @Schema(implementation = ErrorDto.class))
)
public @interface ApiResponseNotFound {
}
