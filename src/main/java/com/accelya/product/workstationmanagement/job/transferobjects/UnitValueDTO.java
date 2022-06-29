package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UnitValueDTO implements Serializable {
    @NotNull
    private double value;
    @Valid
    @NotNull(message = "unit cannot be blank")
    private UnitDTO unit;
}
