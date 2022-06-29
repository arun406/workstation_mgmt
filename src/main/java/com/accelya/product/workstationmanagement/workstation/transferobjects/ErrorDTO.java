package com.accelya.product.workstationmanagement.workstation.transferobjects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ErrorDTO implements Serializable {
    private String errorCode;
    private String type;
    private String message;
    private String longMessage;
    private String severity;
}
