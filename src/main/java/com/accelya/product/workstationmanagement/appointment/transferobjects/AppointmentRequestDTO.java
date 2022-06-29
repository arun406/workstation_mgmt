package com.accelya.product.workstationmanagement.appointment.transferobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppointmentRequestDTO implements Serializable {

    private List<String> tags;
    //    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZ")
    private OffsetDateTime fromTime;
    //    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZ")
    private OffsetDateTime toTime;
    private Duration duration;
    private Integer jobId;
    private Integer workstationId;
    private boolean ignoreULDTypeCheck;
}
