package com.accelya.product.workstationmanagement.job.model;

import com.accelya.product.workstationmanagement.ListToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "WA_JOB_PARAM_SHPMT")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WJPS_SEQ")
    @SequenceGenerator(name = "WJPS_SEQ", sequenceName = "WJPS_SEQ")
    @Column(name = "WJPS_ID")
    private Integer id;
    @Column(length = 8, name = "DOC_NUM")
    private String documentNumber;
    @Column(length = 3, name = "DOC_PFX")
    private String documentPrefix;
    @Column(length = 4, name = "DOC_TYP")
    private String documentType;
    private Integer piece;
    private Double weight;
    private Double volume;
    private String weightUnit;
    private String volumeUnit;
    @Type(type = "yes_no")
    private boolean eAWBIndicator;
    @Column(name = "ORG", length = 3)
    private String origin;
    @Column(name = "DEST", length = 3)
    private String destination;
    private String commodity;
    @Convert(converter = ListToStringConverter.class)
    private List<String> shc;
    private String description;
    private String productCode;
    private String bookingStatus;
    private String remarks;
    private String routing;
    @Column(length = 1)
    private String status;
    private Integer actionedPiece;
    private Double actionedWeight;
    private Double actionedVolume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WJP_ID", nullable = false, referencedColumnName = "WJP_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private JobParameters jobParameters;

    private String tenant;
    private Integer transactionId;
    private String createdBy;
    private ZonedDateTime createdDate;
    private String modifiedBy;
    private ZonedDateTime modifiedDate;

    @PreRemove
    public void removeJobParameter() {
        this.jobParameters.removeShipment(this);
        this.jobParameters = null;
    }

    //////////////////////////////////////////// Setters and Getters ///////////////////////////////////////////////////
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentPrefix() {
        return documentPrefix;
    }

    public void setDocumentPrefix(String documentPrefix) {
        this.documentPrefix = documentPrefix;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Integer getPiece() {
        return piece;
    }

    public void setPiece(Integer piece) {
        this.piece = piece;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
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

    public boolean iseAWBIndicator() {
        return eAWBIndicator;
    }

    public boolean isEAWBIndicator() {
        return eAWBIndicator;
    }

    public void seteAWBIndicator(boolean eAWBIndicator) {
        this.eAWBIndicator = eAWBIndicator;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public List<String> getShc() {
        return shc;
    }

    public void setShc(List<String> shc) {
        this.shc = shc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

    public void setJobParameters(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getActionedPiece() {
        return actionedPiece;
    }

    public void setActionedPiece(Integer actionedPiece) {
        this.actionedPiece = actionedPiece;
    }

    public Double getActionedWeight() {
        return actionedWeight;
    }

    public void setActionedWeight(Double actionedWeight) {
        this.actionedWeight = actionedWeight;
    }

    public Double getActionedVolume() {
        return actionedVolume;
    }

    public void setActionedVolume(Double actionedVolume) {
        this.actionedVolume = actionedVolume;
    }
}
