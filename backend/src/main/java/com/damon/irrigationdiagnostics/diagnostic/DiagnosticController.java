package com.damon.irrigationdiagnostics.diagnostic;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @PostMapping("/telemetry/{telemetryReadingId}")
    public List<PersistedDiagnosticFinding> runDiagnostics(
            @PathVariable Long telemetryReadingId
    ) {
        return diagnosticService.runDiagnostics(telemetryReadingId);
    }
}