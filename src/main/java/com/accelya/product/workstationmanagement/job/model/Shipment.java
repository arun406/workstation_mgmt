package com.accelya.product.workstationmanagement.job.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "WA_JOB_PARAM_SHPMT")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WJPS_SEQ")
    @SequenceGenerator(name = "WJPS_SEQ", sequenceName = "WJPS_SEQ")
    private Integer id;
    private String documentNumber;
    private String documentPrefix;
    private String documentType;
    private Integer piece;
    private Double weight;
    private Double volume;
    private String weightUnit;
    private String volumeUnit;
    private boolean eAWBIndicator;
    private String origin;
    private String destination;
    private String commodity;
    private String shcList;
    private String description;
    private String productCode;
    private String bookingStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "WJP_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private JobParameters jobParameters;
}
