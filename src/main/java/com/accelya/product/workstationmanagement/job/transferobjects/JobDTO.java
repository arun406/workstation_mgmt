package com.accelya.product.workstationmanagement.job.transferobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.OffsetDateTime;

@Data
@Builder
@ToString
public class JobDTO {
    private Integer id;
    @NotBlank
    @Size(min = 0, max = 20)
    private String code;
    @NotBlank
    @Size(min = 0, max = 10)
    private String type;
    @Size(min = 0, max = 20)
    private String groupCode;
    @Size(min = 0, max = 100)
    private String groupName;
    private Boolean updatable;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZ")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mmZ")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime endTime;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdDate;
    private String modifiedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime modifiedDate;
    private String status;
    private Duration duration;
    private Integer priority;
    @Valid
    @NotNull(message = "job parameters cannot be null")
    private JobParametersDTO jobParameters;
    private String notes;
    private String remarks;
    private String completionRemarks;
}
