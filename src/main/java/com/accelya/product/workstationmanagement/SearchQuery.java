package com.accelya.product.workstationmanagement;

import lombok.Data;

import java.util.List;

@Data
public class SearchQuery {
    private List<SearchCriteria> searchCriteria;
    private int pageNumber;
    private int pageSize;
    private SortOrder sortOrder;
    private List<JoinColumnProps> joinColumnProps;
}
