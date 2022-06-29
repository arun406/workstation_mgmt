package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UnitDTO implements Serializable {
    @NotNull(message = "unit code cannot be null")
    private String code;
    private String description;
}
