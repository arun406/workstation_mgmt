package com.accelya.product.workstationmanagement.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "WA_JOB_PARAM_FLT")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WJPF_SEQ")
    @SequenceGenerator(name = "WJPF_SEQ", sequenceName = "WJPF_SEQ")
    @Column(name = "WJPF_ID")
    private Integer id;
    private String boardPoint;
    private String offPoint;
    private String aircraftCategory;
    private String iataAircraftType;
    private String aircraftRegistration;
    private String carrier;
    private String flightNumber;
    private String extensionNumber;

    @Basic
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate flightDate;
    private LocalDateTime std;
    private LocalDateTime etd;
    private LocalDateTime atd;
    private LocalDateTime sta;
    private LocalDateTime eta;
    private LocalDateTime ata;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "WJP_ID", referencedColumnName = "WJP_ID")
    private JobParameters jobParameters;

    private String tenant;
    private Integer transactionId;
    private String createdBy;
    private ZonedDateTime createdDate;
    private String modifiedBy;
    private ZonedDateTime modifiedDate;


    public String getBoardPoint() {
        return boardPoint;
    }

    public void setBoardPoint(String boardPoint) {
        this.boardPoint = boardPoint;
    }

    public String getOffPoint() {
        return offPoint;
    }

    public void setOffPoint(String offPoint) {
        this.offPoint = offPoint;
    }

    public String getAircraftCategory() {
        return aircraftCategory;
    }

    public void setAircraftCategory(String aircraftCategory) {
        this.aircraftCategory = aircraftCategory;
    }

    public String getIataAircraftType() {
        return iataAircraftType;
    }

    public void setIataAircraftType(String iataAircraftType) {
        this.iataAircraftType = iataAircraftType;
    }

    public String getAircraftRegistration() {
        return aircraftRegistration;
    }

    public void setAircraftRegistration(String aircraftRegistration) {
        this.aircraftRegistration = aircraftRegistration;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }


    public LocalDateTime getStd() {
        return std;
    }

    public void setStd(LocalDateTime std) {
        this.std = std;
    }

    public LocalDateTime getEtd() {
        return etd;
    }

    public void setEtd(LocalDateTime etd) {
        this.etd = etd;
    }

    public LocalDateTime getAtd() {
        return atd;
    }

    public void setAtd(LocalDateTime atd) {
        this.atd = atd;
    }

    public LocalDateTime getSta() {
        return sta;
    }

    public void setSta(LocalDateTime sta) {
        this.sta = sta;
    }

    public LocalDateTime getEta() {
        return eta;
    }

    public void setEta(LocalDateTime eta) {
        this.eta = eta;
    }

    public LocalDateTime getAta() {
        return ata;
    }

    public void setAta(LocalDateTime ata) {
        this.ata = ata;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }

    public LocalDate getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(LocalDate flightDate) {
        this.flightDate = flightDate;
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
