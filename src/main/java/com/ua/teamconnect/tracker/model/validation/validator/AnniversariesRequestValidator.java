package com.ua.teamconnect.tracker.model.validation.validator;

import com.ua.teamconnect.tracker.model.dto.in.AnniversariesDto;
import com.ua.teamconnect.tracker.model.validation.AnniversariesRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AnniversariesRequestValidator implements ConstraintValidator<AnniversariesRequest, AnniversariesDto> {

    @Override
    public boolean isValid(AnniversariesDto value, ConstraintValidatorContext context) {
        return value.startMonth() < value.endMonth()
            || (value.startMonth() == value.endMonth() && value.startDay() <= value.endDay());
    }
}
