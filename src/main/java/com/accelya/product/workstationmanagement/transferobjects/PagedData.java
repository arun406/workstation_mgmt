package com.accelya.product.workstationmanagement.transferobjects;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedData<T> {
    private PageInfo pageInfo;
    private List<T> list;
}
