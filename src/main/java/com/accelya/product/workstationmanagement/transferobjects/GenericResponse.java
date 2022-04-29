package com.accelya.product.workstationmanagement.transferobjects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericResponse<T> {
    private String status;
    private T data;
}
