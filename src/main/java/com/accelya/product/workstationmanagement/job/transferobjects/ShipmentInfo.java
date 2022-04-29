package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShipmentInfo implements Serializable {
    private QuantityInfo quantity;
    private String documentNumber;
    private String documentPrefix;
    private String documentType;
    private boolean eAWBIndicator;
    private StationInfo origin;
    private StationInfo destination;
    private String commodity;
    private String shcList;
    private String description;
    private String cargoReference;
    private String productCode;
    private String bookingStatus;
}
