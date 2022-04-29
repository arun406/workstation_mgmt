package com.accelya.product.workstationmanagement.job.model;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "WA_JOB_PARAM")
public class JobParameters {

    @Id
    @Column(name = "WJP_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WJP_SEQ")
    @SequenceGenerator(name = "WJP_SEQ", sequenceName = "WJP_SEQ")
    private Integer id;
    private String groupCode;
    private String groupName;
    private Integer uldCount;
    private Integer shipmentCount;


    @OneToMany(mappedBy = "jobParameters", cascade = CascadeType.ALL)
    private Set<ULD> uldList;

    @OneToMany(mappedBy = "jobParameters", cascade = CascadeType.ALL)
    private List<Shipment> shipmentList;
    private String boardPoint;
    private String offPoint;
    private String aircraftCategory;
    private String iataAircraftType;
    private String aircraftRegistration;
    private String carrier;
    private String flightNumber;
    private String extensionNumber;
    private Integer loadPlanVersion;
    public LocalDateTime std;
    public LocalDateTime etd;
    public LocalDateTime atd;
    public LocalDateTime sta;
    public LocalDateTime eta;
    public LocalDateTime ata;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "WAJ_ID", referencedColumnName = "WAJ_ID")
    private Job job;
}
