package com.accelya.product.workstationmanagement.job.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "WA_JOB_PARAM")
public class JobParameters {

    @Id
    @Column(name = "WJP_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WJP_SEQ")
    @SequenceGenerator(name = "WJP_SEQ", sequenceName = "WJP_SEQ")
    private Integer id;
    private String uldGroupCode;
    private String uldGroupName;
    private Integer uldCount;
    private Integer shipmentCount;
    private Integer loadPlanVersion;
    private Boolean grouped;
    @OneToMany(mappedBy = "jobParameters", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ULD> ulds;

    @OneToMany(mappedBy = "jobParameters", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Shipment> shipments;

    @OneToOne(mappedBy = "jobParameters", cascade = CascadeType.ALL)
    private Flight flight;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "WAJ_ID", referencedColumnName = "WAJ_ID")
    private Job job;

    private String tenant;
    private Integer transactionId;
    private String createdBy;
    private ZonedDateTime createdDate;
    private String modifiedBy;
    private ZonedDateTime modifiedDate;


    public void removeShipment(Shipment shipment) {
        this.shipments.remove(shipment);
    }

    @PreRemove
    public void removeShipments() {
        this.shipments.forEach(shipment -> shipment.removeJobParameter());
    }

    public void removeShipments(Set<Integer> ids) {
        this.shipments.stream()
                .filter(shipment -> ids.contains(shipment.getId()))
                .forEach(shipment -> shipment.removeJobParameter());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUldGroupCode() {
        return uldGroupCode;
    }

    public void setUldGroupCode(String uldGroupCode) {
        this.uldGroupCode = uldGroupCode;
    }

    public String getUldGroupName() {
        return uldGroupName;
    }

    public void setUldGroupName(String uldGroupName) {
        this.uldGroupName = uldGroupName;
    }

    public Integer getUldCount() {
        return uldCount;
    }

    public void setUldCount(Integer uldCount) {
        this.uldCount = uldCount;
    }

    public Integer getShipmentCount() {
        return shipmentCount;
    }

    public void setShipmentCount(Integer shipmentCount) {
        this.shipmentCount = shipmentCount;
    }

    public Boolean getGrouped() {
        return grouped;
    }

    public void setGrouped(Boolean grouped) {
        this.grouped = grouped;
    }

    public List<ULD> getUlds() {
        return ulds;
    }

    public void setUlds(List<ULD> ulds) {
        this.ulds = ulds;
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Integer getLoadPlanVersion() {
        return loadPlanVersion;
    }

    public void setLoadPlanVersion(Integer loadPlanVersion) {
        this.loadPlanVersion = loadPlanVersion;
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

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ZonedDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(ZonedDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
