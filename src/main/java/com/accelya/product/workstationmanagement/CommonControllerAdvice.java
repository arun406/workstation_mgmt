package com.accelya.product.workstationmanagement;

import com.accelya.product.workstationmanagement.transferobjects.ErrorDTO;
import com.accelya.product.workstationmanagement.transferobjects.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@org.springframework.web.bind.annotation.ControllerAdvice
public class CommonControllerAdvice {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<GenericResponse<ErrorDTO>> handleResponseStatusException(ResponseStatusException rse) {

        GenericResponse<ErrorDTO> errorResponse = GenericResponse.<ErrorDTO>builder()
                .status("failure")
                .data(new ErrorDTO("NOT_FOUND", "E", rse.getReason(), rse.getLocalizedMessage(), "H"))
                .build();

        return new ResponseEntity<>(errorResponse, rse.getStatus());
    }
}
