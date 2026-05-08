package com.ua.teamconnect.tracker.model.validation;

import com.ua.teamconnect.tracker.model.validation.validator.AnniversariesRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AnniversariesRequestValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnniversariesRequest {

    String message() default "Invalid date rage. Start date should be less or equal than end date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
