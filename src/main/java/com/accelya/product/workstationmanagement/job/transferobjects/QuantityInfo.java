package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuantityInfo implements Serializable {
    private Integer piece;
    private UnitValue weight;
    private UnitValue volume;
}
