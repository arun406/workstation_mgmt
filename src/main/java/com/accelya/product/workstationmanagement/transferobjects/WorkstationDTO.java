package com.accelya.product.workstationmanagement.transferobjects;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class WorkstationDTO {

    private Integer id;
    private String airportCode;
    private String warehouseCode;
    private String section;
    private String type;
    private String name;
    private String compatibleTypes;
    private String size;
    private String shc;
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
