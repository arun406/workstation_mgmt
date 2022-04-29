package com.accelya.product.workstationmanagement.loadplan.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "WA_FLT_LOAD_PLN")
public class FlightLoadPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wflp_seq")
    @SequenceGenerator(name = "wflp_seq", allocationSize = 1, sequenceName = "FLP_SEQ")
    private Integer id;
    private Integer version;
    private String description;

}
