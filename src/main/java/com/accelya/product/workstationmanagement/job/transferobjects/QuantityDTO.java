package com.accelya.product.workstationmanagement.job.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class QuantityDTO implements Serializable {

    @NotNull(message = "pieces cannot be blank")
    @Min(0)
    @JsonProperty("pieces")
    private Integer piece;
    @Valid
    @NotNull(message = "weight cannot be blank")
    private UnitValueDTO weight;
    @Valid
    @NotNull(message = "volume cannot be blank")
    private UnitValueDTO volume;
}
