package com.ua.teamconnect.tracker.model.pojo.converter;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneConverterTest {

    private final PhoneConverter phoneConverter = new PhoneConverter();

    @Test
    void convertToDatabaseColumn_nonEmptyMap_validJson() {
        var json = phoneConverter.convertToDatabaseColumn(
            Map.of(
                "home", "+123456789",
                "mobile", "+987654321"
            )
        );
        var path = JsonPath.parse(json);
        assertEquals("+123456789", path.read("$.home"));
        assertEquals("+987654321", path.read("$.mobile"));
    }

    @Test
    void convertToEntityAttribute_validJson_nonEmptyMap() {
        var json = "{\"home\":\"+123456789\",\"mobile\":\"+987654321\"}";
        var map = phoneConverter.convertToEntityAttribute(json);
        var expected = Map.of(
            "home", "+123456789",
            "mobile", "+987654321"
        );
        assertEquals(expected, map);
    }

    @Test
    void convertToEntityAttribute_invalidJson_throwsException() {
        assertThrows(RuntimeException.class,
            () -> phoneConverter.convertToEntityAttribute("invalid json")
        );
    }
}
