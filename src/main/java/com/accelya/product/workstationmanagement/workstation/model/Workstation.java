package com.accelya.product.workstationmanagement.workstation.model;

import com.accelya.product.workstationmanagement.ListToStringConverter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "WA_WORKSTATION")
@AllArgsConstructor
@NoArgsConstructor
public class Workstation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ws_seq")
    @SequenceGenerator(name = "ws_seq", sequenceName = "WS_SEQ", allocationSize = 1)
    @Column(name = "WAW_ID")
    private Integer id;
    @Column(unique = true, name = "WS_CODE")
    private String code;
    private String name;
    private String airport;
    private String warehouse;
    private String section;
    private String type;

    @Convert(converter = ListToStringConverter.class)
    private List<String> compatibleTypes;
    @Column(name = "WS_SIZE")
    private Double size;

    @Convert(converter = ListToStringConverter.class)
    private List<String> shc;
    private String productType;
    @Type(type = "yes_no")
    private Boolean open;
    private LocalTime breakTimeStart;
    private LocalTime breakTimeEnd;
    @Type(type = "yes_no")
    private Boolean serviceable;
    @Column(name = "MULTIPLE_ULD_ALLOWED")
    @Type(type = "yes_no")
    private Boolean multipleULDAllowed;
    @Type(type = "yes_no")
    private Boolean fixed;
    private String notificationTime;
    @Type(type = "yes_no")
    private Boolean active;

/*
    @OneToMany(mappedBy = "workstation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Appointment> appointments;
*/

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCompatibleTypes() {
        return compatibleTypes;
    }

    public void setCompatibleTypes(List<String> compatibleTypes) {
        this.compatibleTypes = compatibleTypes;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public List<String> getShc() {
        return shc;
    }

    public void setShc(List<String> shc) {
        this.shc = shc;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public LocalTime getBreakTimeStart() {
        return breakTimeStart;
    }

    public void setBreakTimeStart(LocalTime breakTimeStart) {
        this.breakTimeStart = breakTimeStart;
    }

    public LocalTime getBreakTimeEnd() {
        return breakTimeEnd;
    }

    public void setBreakTimeEnd(LocalTime breakTimeEnd) {
        this.breakTimeEnd = breakTimeEnd;
    }

    public Boolean getServiceable() {
        return serviceable;
    }

    public void setServiceable(Boolean serviceable) {
        this.serviceable = serviceable;
    }

    public Boolean getMultipleULDAllowed() {
        return multipleULDAllowed;
    }

    public void setMultipleULDAllowed(Boolean multipleULDAllowed) {
        this.multipleULDAllowed = multipleULDAllowed;
    }

    public Boolean getFixed() {
        return fixed;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

/*
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
*/

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
