package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

@Data
public class UldDetail {
    private String transferHandlingCode;
    private String carrierCode;
    private String uldType;
    private String contourCode;
    private String status;
    private String rateType;
    private String priorityCode;
    private String loadingCode;
    private UnitValue tareWeight;
    private UnitValue maximumWeight;
    private UnitValue actualWeight;
    private UnitValue maximumVolume;
    private UnitValue actualVolume;
    private Integer totalPieces;
    private Integer totalShipments;
    private int slac;
}
