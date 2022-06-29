package com.accelya.product.workstationmanagement;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
@Slf4j
public class ListToStringConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        log.debug("converting list to string");
        return list != null ? String.join(",", list) : null;
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        log.debug("converting delimited string to list");
        return joined != null ? new ArrayList<>(Arrays.asList(joined.split(","))) : null;
    }
}
