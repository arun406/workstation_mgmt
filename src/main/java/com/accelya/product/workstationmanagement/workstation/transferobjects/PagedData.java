package com.accelya.product.workstationmanagement.workstation.transferobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedData<T> {
    private PageInfo pageInfo;
    private List<T> list;
}
