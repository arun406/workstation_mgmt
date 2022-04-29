package com.accelya.product.workstationmanagement.transferobjects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageInfo {
    private Integer pageNumber;
    private Long listSize;
    private Integer pageSize;
    private Integer totalPages;
}
