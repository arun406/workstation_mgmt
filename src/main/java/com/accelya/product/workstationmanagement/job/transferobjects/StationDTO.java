package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class StationDTO {
    @NotBlank(message = "airport code cannot be blank")
    private String code;
    private String type;
}
