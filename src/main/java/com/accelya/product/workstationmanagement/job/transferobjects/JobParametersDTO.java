package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class JobParametersDTO implements Serializable {
    @NotNull(message = "load plan version cannot be null")
    private Integer loadPlanVersion;
    private Boolean grouped;
    private String uldGroupCode;
    private String uldGroupName;
    private Integer uldCount;
    @Valid
    @NotNull
    private List<UldDTO> ulds;
    @Valid
    @NotNull
    private List<ShipmentDTO> shipments;
    @Valid
    @NotNull(message = "flight cannot be null")
    private FlightDTO flight;
}
