package com.accelya.product.workstationmanagement.job.model;


import com.accelya.product.workstationmanagement.ListToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private String uldSerialNumber;
    private String name;
    private String contourCode;
    private String status;
    private String rateType;
    private String priorityCode;
    private String loadingCode;
    @Convert(converter = ListToStringConverter.class)
    private List<String> shc;
    private Double tareWeight;
    private Double maximumWeight;
    private Double actualWeight;
    private Double maximumVolume;
    private Double actualVolume;
    private String weightUnit;
    private String volumeUnit;
    private Integer totalPieces;
    private Integer totalShipments;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "WJP_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private JobParameters jobParameters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransferHandlingCode() {
        return transferHandlingCode;
    }

    public void setTransferHandlingCode(String transferHandlingCode) {
        this.transferHandlingCode = transferHandlingCode;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getUldType() {
        return uldType;
    }

    public void setUldType(String uldType) {
        this.uldType = uldType;
    }

    public String getContourCode() {
        return contourCode;
    }

    public void setContourCode(String contourCode) {
        this.contourCode = contourCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public String getLoadingCode() {
        return loadingCode;
    }

    public void setLoadingCode(String loadingCode) {
        this.loadingCode = loadingCode;
    }

    public Double getTareWeight() {
        return tareWeight;
    }

    public void setTareWeight(Double tareWeight) {
        this.tareWeight = tareWeight;
    }

    public Double getMaximumWeight() {
        return maximumWeight;
    }

    public void setMaximumWeight(Double maximumWeight) {
        this.maximumWeight = maximumWeight;
    }

    public Double getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(Double actualWeight) {
        this.actualWeight = actualWeight;
    }

    public Double getMaximumVolume() {
        return maximumVolume;
    }

    public void setMaximumVolume(Double maximumVolume) {
        this.maximumVolume = maximumVolume;
    }

    public Double getActualVolume() {
        return actualVolume;
    }

    public void setActualVolume(Double actualVolume) {
        this.actualVolume = actualVolume;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(String volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    public Integer getTotalPieces() {
        return totalPieces;
    }

    public void setTotalPieces(Integer totalPieces) {
        this.totalPieces = totalPieces;
    }

    public Integer getTotalShipments() {
        return totalShipments;
    }

    public void setTotalShipments(Integer totalShipments) {
        this.totalShipments = totalShipments;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }

    public String getUldSerialNumber() {
        return uldSerialNumber;
    }

    public void setUldSerialNumber(String uldSerialNumber) {
        this.uldSerialNumber = uldSerialNumber;
    }

    public List<String> getShc() {
        return shc;
    }

    public void setShc(List<String> shc) {
        this.shc = shc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
