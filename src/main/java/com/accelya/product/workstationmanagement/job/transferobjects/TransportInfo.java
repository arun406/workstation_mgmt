package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransportInfo implements Serializable {
    private String carrier;
    private String number;
    private String extensionNumber;
}
