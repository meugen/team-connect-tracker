package com.ua.teamconnect.tracker.model.pojo.converter;

import com.ua.teamconnect.tracker.model.pojo.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        return attribute.name();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        return Gender.valueOf(dbData);
    }
}
