package com.accelya.product.workstationmanagement.appointment.transferobjects;

import com.accelya.product.workstationmanagement.job.transferobjects.JobDTO;
import com.accelya.product.workstationmanagement.workstation.transferobjects.WorkstationDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@ToString
@Builder
public class AppointmentDTO implements Serializable {
    private Integer id;
    private List<String> tags;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZ")
    private OffsetDateTime fromTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZ")
    private OffsetDateTime toTime;
    private String status;
    private JobDTO job;
    private WorkstationDTO workstation;
}
