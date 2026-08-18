package com.damon.irrigationdiagnostics.telemetry;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TelemetryNotFoundException extends RuntimeException {

    public TelemetryNotFoundException(Long id) {
        super("Telemetry reading not found with id: " + id);
    }
}