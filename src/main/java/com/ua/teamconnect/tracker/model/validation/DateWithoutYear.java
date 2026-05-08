package com.ua.teamconnect.tracker.model.validation;

import com.ua.teamconnect.tracker.model.validation.validator.DateWithoutYearValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateWithoutYearValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateWithoutYear {
}
