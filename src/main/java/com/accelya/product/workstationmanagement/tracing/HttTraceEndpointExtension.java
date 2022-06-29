package com.accelya.product.workstationmanagement.tracing;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.boot.actuate.trace.http.HttpTraceEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "management.trace.http", name = "enabled", matchIfMissing = true)
@EndpointWebExtension(endpoint = HttpTraceEndpoint.class)
public class HttTraceEndpointExtension {
    private DatabaseHttpTraceRepository repository;

    public HttTraceEndpointExtension(DatabaseHttpTraceRepository repository) {
        this.repository = repository;
    }

    @ReadOperation
    public ContentTraceDescriptor contents() {
        List<ContentTrace> traces = repository.findAllWithContent();
        return new ContentTraceDescriptor(traces);
    }
}
