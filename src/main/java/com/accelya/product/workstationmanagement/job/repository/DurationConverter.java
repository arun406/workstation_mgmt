package com.accelya.product.workstationmanagement.job.repository;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {
    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        if (duration != null)
            return duration.toMinutes();
        return 0L;
    }

    @Override
    public Duration convertToEntityAttribute(Long duration) {
        return Duration.of(duration, ChronoUnit.MINUTES);
    }
}
