package com.accelya.product.workstationmanagement;

import lombok.Data;

import java.util.List;

@Data
public class SortOrder {
    private List<String> ascendingOrder;
    private List<String> descendingOrder;
}
