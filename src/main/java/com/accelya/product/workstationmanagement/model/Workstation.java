package com.accelya.product.workstationmanagement.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@Table(name = "WA_WORKSTATION")
public class Workstation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ws_seq")
    @SequenceGenerator(name = "ws_seq", sequenceName = "WS_SEQ", allocationSize = 1)
    @Column(name = "waw_id")
    private Integer id;
    private String airportCode;
    private String warehouseCode;
    private String section;
    private String type;
    private String name;
    private String compatibleTypes;
    @Column(name = "WS_SIZE")
    private String size;
    private String shc;
    private String productType;

    @Type(type = "true_false")
    private Boolean open;
    private LocalTime breakTimeStart;
    private LocalTime breakTimeEnd;
    @Type(type = "true_false")
    private Boolean serviceable;
    @Column(name = "MULTIPLE_ULD_ALLOWED")
    @Type(type = "true_false")
    private Boolean multipleULDAllowed;
    @Type(type = "true_false")
    private Boolean fixed;
    private String notificationTime;
    @Type(type = "true_false")
    private Boolean active;
}
