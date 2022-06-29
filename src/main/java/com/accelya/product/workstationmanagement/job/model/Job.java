package com.accelya.product.workstationmanagement.job.model;

import com.accelya.product.workstationmanagement.appointment.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.OffsetDateTime;

@Entity
@Table(name = "WA_JOB")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WA_JOB_GEN")
    @SequenceGenerator(name = "WA_JOB_GEN", sequenceName = "WJ_SEQ", allocationSize = 1)
    @Column(name = "WAJ_ID")
    private Integer id;
    private String code;
    private String type;

    @Column(name = "GRP_CODE")
    private String groupCode;
    private String groupName;
    private Boolean updatable;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    private Duration duration;
    private String status;

    private String remarks;
    private String notes;
    private Integer priority;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PARENT_JOB_ID")
    private Job parent;

    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
    private JobParameters jobParameters;

    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
    private Appointment appointment;
    private String completionRemarks;


    private String tenant;
    private Integer transactionId;
    private String createdBy;
    private OffsetDateTime createdDate;
    private String modifiedBy;
    private OffsetDateTime modifiedDate;

    /////////////////////////////Setters and Getters ///////////////////////////////////////////////////////////////////
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean getUpdatable() {
        return updatable;
    }

    public void setUpdatable(Boolean updatable) {
        this.updatable = updatable;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }

    public Job getParent() {
        return parent;
    }

    public void setParent(Job parent) {
        this.parent = parent;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCompletionRemarks() {
        return completionRemarks;
    }

    public void setCompletionRemarks(String completionRemarks) {
        this.completionRemarks = completionRemarks;
    }
}
