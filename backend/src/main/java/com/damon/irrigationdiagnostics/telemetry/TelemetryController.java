package com.damon.irrigationdiagnostics.telemetry;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping
    public TelemetryReading createTelemetry(
            @Valid @RequestBody CreateTelemetryRequest request
    ) {
        return telemetryService.createTelemetry(request);
    }

    @GetMapping("/zone/{zoneId}")
    public List<TelemetryReading> getTelemetryForZone(@PathVariable Long zoneId) {
        return telemetryService.getTelemetryForZone(zoneId);
    }
}