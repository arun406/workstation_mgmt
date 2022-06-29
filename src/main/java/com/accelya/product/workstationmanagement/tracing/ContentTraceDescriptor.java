package com.accelya.product.workstationmanagement.tracing;

import lombok.Data;

import java.util.List;

@Data
public class ContentTraceDescriptor {
    protected List<ContentTrace> traces;

    public ContentTraceDescriptor(List<ContentTrace> traces) {
        super();
        this.traces = traces;
    }
}
