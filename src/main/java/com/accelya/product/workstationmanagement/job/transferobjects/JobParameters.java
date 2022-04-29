package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JobParameters implements Serializable {
    private Integer loadPlanVersion;
    private String groupCode;
    private String groupName;
    private Integer uldCount;
    private List<UldDetail> uldList;
    private List<ShipmentInfo> shipmentList;
    private FlightInfo flightInfo;
}
