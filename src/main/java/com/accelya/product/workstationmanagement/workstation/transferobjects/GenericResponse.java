package com.accelya.product.workstationmanagement.workstation.transferobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private String status;
    private T data;
}
