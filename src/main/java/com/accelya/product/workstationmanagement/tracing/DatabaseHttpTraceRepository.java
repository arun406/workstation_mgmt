package com.accelya.product.workstationmanagement.tracing;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(prefix = "management.trace.http", name = "enabled", matchIfMissing = true)
public class DatabaseHttpTraceRepository implements HttpTraceRepository {

    private final List<ContentTrace> contents = new LinkedList<>();

    private ContentTraceManager traceManager;

    public DatabaseHttpTraceRepository(ContentTraceManager traceManager) {
        this.traceManager = traceManager;
    }

    @Override
    public List<HttpTrace> findAll() {
        synchronized (this.contents) {
            return contents.stream().map(ContentTrace::getHttpTrace)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void add(HttpTrace trace) {
        synchronized (this.contents) {
            ContentTrace contentTrace = traceManager.getTrace();
            contentTrace.setHttpTrace(trace);
            this.contents.add(0, contentTrace);
        }
    }

    public List<ContentTrace> findAllWithContent() {
        synchronized (this.contents) {
            return Collections.unmodifiableList(new ArrayList<>(this.contents));
        }
    }

}
