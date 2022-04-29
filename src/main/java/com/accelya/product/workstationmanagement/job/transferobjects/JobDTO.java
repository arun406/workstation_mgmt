package com.accelya.product.workstationmanagement.job.transferobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
public class JobDTO {
    private Integer id;
    private String code;
    private String type;
    private String groupCode;
    private String groupName;
    private Boolean updatable;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private ZonedDateTime endTime;
    private String createdBy;
    private ZonedDateTime createdDate;
    private String modifiedBy;
    private ZonedDateTime modifiedDate;
    private String status;
    private Duration duration;
    private Map<String, String> metadata;
    private Integer priority;
}
