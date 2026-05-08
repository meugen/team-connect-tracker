package com.ua.teamconnect.tracker.model.validation.group;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

/**
 * Defines the validation sequence for AnniversariesDto:
 * 1. Default group (includes unassigned validations)
 * 2. BasicValidation (NotNull, NotEmpty)
 * 3. FormatValidation (DateWithoutYear)
 * 4. Cross-field validation (AnniversariesRequest)
 */
@GroupSequence({Default.class, BasicValidation.class, FormatValidation.class})
public interface CompleteValidation {
}

