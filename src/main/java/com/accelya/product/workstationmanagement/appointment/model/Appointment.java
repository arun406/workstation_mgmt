package com.accelya.product.workstationmanagement.appointment.model;

import com.accelya.product.workstationmanagement.ListToStringConverter;
import com.accelya.product.workstationmanagement.job.model.Job;
import com.accelya.product.workstationmanagement.workstation.model.Workstation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WA_APPOINTMENT")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @Column(name = "WAA_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WA_APP_GEN")
    @SequenceGenerator(name = "WA_APP_GEN", sequenceName = "WAP_SEQ")
    private Integer id;

    private OffsetDateTime fromTime;
    private OffsetDateTime toTime;

    @Convert(converter = ListToStringConverter.class)
    private List<String> tags = new ArrayList<>();
    private String status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "WAJ_ID", referencedColumnName = "WAJ_ID")
    private Job job;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "WAW_ID", nullable = false, referencedColumnName = "WAW_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workstation workstation;

    private String tenant;
    private Integer transactionId;
    private String createdBy;
    private OffsetDateTime createdDate;
    private String modifiedBy;
    private OffsetDateTime modifiedDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OffsetDateTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(OffsetDateTime fromTime) {
        this.fromTime = fromTime;
    }

    public OffsetDateTime getToTime() {
        return toTime;
    }

    public void setToTime(OffsetDateTime toTime) {
        this.toTime = toTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Workstation getWorkstation() {
        return workstation;
    }

    public void setWorkstation(Workstation workstation) {
        this.workstation = workstation;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public OffsetDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(OffsetDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
