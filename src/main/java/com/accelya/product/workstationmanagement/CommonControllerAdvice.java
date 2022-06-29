package com.accelya.product.workstationmanagement;

import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentNotExists;
import com.accelya.product.workstationmanagement.workstation.transferobjects.ErrorDTO;
import com.accelya.product.workstationmanagement.workstation.transferobjects.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ExceptionHandler(AppointmentNotExists.class)
    public ResponseEntity<GenericResponse<ErrorDTO>> handleResponseStatusException(AppointmentNotExists ane) {

        GenericResponse<ErrorDTO> errorResponse = GenericResponse.<ErrorDTO>builder()
                .status("failure")
                .data(new ErrorDTO("APPOINTMENT_NOT_FOUND", "E", ane.getMessage(), ane.getLocalizedMessage(), "H"))
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<List<ErrorDTO>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<ErrorDTO> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ErrorDTO("ERR0001", "V", "validation failed for field '" + fieldName + "'", errorMessage, "H"));
        });
        GenericResponse<List<ErrorDTO>> errorResponse = GenericResponse.<List<ErrorDTO>>builder()
                .status("failure")
                .data(errors)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
