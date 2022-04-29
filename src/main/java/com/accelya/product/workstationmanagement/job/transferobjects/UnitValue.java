package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnitValue implements Serializable {
    private double value;
    private Unit unit;
}
