package com.ua.teamconnect.tracker.model.pojo.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.teamconnect.tracker.model.pojo.Phone;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PhoneConverter implements AttributeConverter<Phone, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Phone attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Phone convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, Phone.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
