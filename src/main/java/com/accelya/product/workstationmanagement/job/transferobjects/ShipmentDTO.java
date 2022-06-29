package com.accelya.product.workstationmanagement.job.transferobjects;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
public class ShipmentDTO implements Serializable {
    @NotNull(message = "quantity cannot be blank")
    @Valid
    private QuantityDTO quantity;

    @NotBlank(message = "document number cannot be blank")
    @Size(min = 8, max = 8)
    private String documentNumber;

    @NotBlank(message = "document prefix cannot be blank")
    @Size(min = 3, max = 3)
    private String documentPrefix;

    @NotBlank(message = "document type cannot be blank")
    private String documentType;

    private boolean eAWBIndicator;

    @NotNull(message = "origin cannot be blank")
    @Valid
    private StationDTO origin;

    @NotNull(message = "destination cannot be blank")
    @Valid
    private StationDTO destination;

    private String commodity;
    @Size(max = 9, message = "maximum allowed shc per shipment is 9")
    private List<String> shc;
    private String description;
    private String cargoReference;

    @NotBlank(message = "product code cannot be blank")
    private String productCode;

    @NotBlank(message = "booking status cannot be blank")
    @Size(min = 2, max = 2)
    private String bookingStatus;
    private String remarks;
    @NotBlank(message = "routing cannot be blank")
    private String routing;
    private String status;
}
