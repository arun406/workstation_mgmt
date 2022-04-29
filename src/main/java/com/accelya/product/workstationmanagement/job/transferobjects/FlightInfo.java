package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

@Data
public class FlightInfo {
    private StationInfo boardPoint;
    private StationInfo offPoint;
    private String aircraftCategory;
    private String iataAircraftType;
    private String aircraftRegistration;
    private TransportInfo transportInfo;
    private TransportTime departureDateTimeLocal;
    private TransportTime departureDateTimeUTC;
    private TransportTime arrivalDateTimeLocal;
    private TransportTime arrivalDateTimeUTC;
}
