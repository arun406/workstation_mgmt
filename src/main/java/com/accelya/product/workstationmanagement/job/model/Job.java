package com.accelya.product.workstationmanagement.job.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "WA_JOB")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wj_seq")
    @SequenceGenerator(name = "wj_seq", sequenceName = "Wj_SEQ", allocationSize = 1)
    @Column(name = "waj_ID")
    private Integer id;
    private String code;
    @Column(name = "GRP_CODE")
    private String groupCode;
    private String type;
    private String groupName;
    private Boolean updatable;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private String createdBy;
    private ZonedDateTime createdDate;
    private String modifiedBy;
    private ZonedDateTime modifiedDate;
    private Duration duration;
    private String status;
    @Column(name = "FLT_LOAD_PLN_VER")
    private Integer flightLoadPlanVersion;
    @ElementCollection
    @CollectionTable(name = "wa_job_metadata",
            joinColumns = {@JoinColumn(name = "waj_ID", referencedColumnName = "waj_ID")}
    )
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> metadata;
    private Integer priority;

    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private JobParameters jobParameters;

}
