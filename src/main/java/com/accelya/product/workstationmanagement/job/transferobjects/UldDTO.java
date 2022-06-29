package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UldDTO {
    private String transferHandlingCode;

    @NotBlank(message = "carrier code not be blank")
    private String carrierCode;

    @NotNull
    private String uldType;
    private String uldSerialNumber;
    private String name;
    private String contourCode;
    private String status;
    private String rateType;
    private String priorityCode;
    private List<String> shc;
    private String loadingCode;
    private UnitValueDTO tareWeight;
    private UnitValueDTO maximumWeight;
    private UnitValueDTO actualWeight;
    private UnitValueDTO maximumVolume;
    private UnitValueDTO actualVolume;
    private Integer totalPieces;
    private Integer totalShipments;
    private int slac;
}
