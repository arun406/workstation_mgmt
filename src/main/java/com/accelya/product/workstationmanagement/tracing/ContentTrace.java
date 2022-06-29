package com.accelya.product.workstationmanagement.tracing;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.security.core.Authentication;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ContentTrace {
    HttpTrace httpTrace;
    String requestBody;
    String responseBody;
    Authentication principal;
}
