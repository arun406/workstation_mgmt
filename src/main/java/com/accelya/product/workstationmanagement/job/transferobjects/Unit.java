package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import java.io.Serializable;

@Data
public class Unit implements Serializable {
    private String code;
    private String description;
}
