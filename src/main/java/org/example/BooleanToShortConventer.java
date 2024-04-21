package org.example;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)//not for Hibernate Native
public class BooleanToShortConventer implements AttributeConverter<Boolean, Short> {
    @Override
    public Short convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute ? (short) 1 : (short) 0;
    }
    @Override
    public Boolean convertToEntityAttribute(Short value) {
        if (value == null) {
            return null;
        }
        if (value == 1) {
            return true;
        }
        if (value == 0) {
            return false;
        }

        return false;
    }
}