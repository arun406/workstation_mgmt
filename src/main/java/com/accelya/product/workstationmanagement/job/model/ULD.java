package com.accelya.product.workstationmanagement.job.model;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
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
@Table(name = "WA_JOB_PARAM_ULD")
public class ULD {

    @Id
    @Column(name = "WJPU_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WJPU_SEQ")
    @SequenceGenerator(name = "WJPU_SEQ", sequenceName = "WJPU_SEQ")
    private Integer id;
    private String transferHandlingCode;
    private String carrierCode;
    private String uldType;
    private String contourCode;
    private String status;
    private String rateType;
    private String priorityCode;
    private String loadingCode;
    private Double tareWeight;
    private Double maximumWeight;
    private Double actualWeight;
    private Double maximumVolume;
    private Double actualVolume;
    private String weightUnit;
    private String volumeUnit;
    private Integer totalPieces;
    private Integer totalShipments;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "WJP_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private JobParameters jobParameters;
}
