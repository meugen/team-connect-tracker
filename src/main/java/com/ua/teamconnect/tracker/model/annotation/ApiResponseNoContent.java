package com.ua.teamconnect.tracker.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ua.teamconnect.tracker.model.dto.ErrorDto;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ApiResponse(
	    responseCode = "204",
	    description = "No content - Request processed successfully. No content returned.",
	    content = @Content(schema = @Schema(implementation = ErrorDto.class))
	)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResponseNoContent {

}
