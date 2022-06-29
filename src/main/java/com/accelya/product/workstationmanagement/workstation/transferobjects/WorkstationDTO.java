package com.accelya.product.workstationmanagement.workstation.transferobjects;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@ToString
public class WorkstationDTO {
    private Integer id;
    @NotBlank
    private String code;
    @NotBlank
    private String airport;
    @NotBlank
    private String warehouse;
    @NotBlank
    private String section;
    @NotBlank
    private String type;
    private String name;
    private List<String> compatibleTypes;
    private Double size;  //Size in Feet
    private List<String> shc;
    private String productType;
    private Boolean open;
    private LocalTime breakTimeStart;
    private LocalTime breakTimeEnd;
    private Boolean serviceable;
    private Boolean multipleULDAllowed;
    private Boolean fixed;
    private String notificationTime;
    private Boolean active;
}
