package com.ua.teamconnect.tracker.model.pojo.converter;

import com.ua.teamconnect.tracker.model.pojo.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenderConverterTest {

    private final GenderConverter converter = new GenderConverter();

    @ParameterizedTest
    @MethodSource("validGenders")
    void convertToDatabaseColumn_validGender_validText(Gender gender, String expected) {
        var actual = converter.convertToDatabaseColumn(gender);
        assertEquals(expected, actual);
    }

    private static List<Arguments> validGenders() {
        return List.of(
            Arguments.of(Gender.MALE, "MALE"),
            Arguments.of(Gender.FEMALE, "FEMALE")
        );
    }

    @ParameterizedTest
    @MethodSource("validTexts")
    void convertToEntityAttribute_validText_validGender(String text, Gender expected) {
        var actual = converter.convertToEntityAttribute(text);
        assertEquals(expected, actual);
    }

    private static List<Arguments> validTexts() {
        return List.of(
            Arguments.of("MALE", Gender.MALE),
            Arguments.of("FEMALE", Gender.FEMALE)
        );
    }

    @Test
    void convertToEntityAttribute_invalidText_throwsException() {
        assertThrows(RuntimeException.class,
            () -> converter.convertToEntityAttribute("invalid")
        );
    }
}
