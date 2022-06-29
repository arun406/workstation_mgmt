package com.accelya.product.workstationmanagement.job.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FlightDTO {
    @NotBlank
    private String boardPoint;
    @NotBlank
    private String offPoint;
    private String aircraftCategory;
    private String iataAircraftType;
    private String aircraftRegistration;
    @Valid
    @NotNull
    private TransportDTO transportInfo;
    private TransportTimeDTO departureDateTimeLocal;
    @JsonIgnore
    private TransportTimeDTO departureDateTimeUTC;
    private TransportTimeDTO arrivalDateTimeLocal;
    @JsonIgnore
    private TransportTimeDTO arrivalDateTimeUTC;
}
