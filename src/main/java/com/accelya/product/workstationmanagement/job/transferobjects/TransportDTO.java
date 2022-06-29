package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class TransportDTO implements Serializable {

    @NotBlank
    private String carrier;
    @NotBlank
    private String number;
    private String extensionNumber;
    @NotNull
    private LocalDate flightDate;
}
