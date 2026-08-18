package com.damon.irrigationdiagnostics.diagnostic;

import com.damon.irrigationdiagnostics.analysis.DiagnosticFinding;
import com.damon.irrigationdiagnostics.analysis.TelemetryAnalyzer;
import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import com.damon.irrigationdiagnostics.telemetry.TelemetryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiagnosticService {

    private final TelemetryRepository telemetryRepository;
    private final TelemetryAnalyzer telemetryAnalyzer;
    private final DiagnosticRunRepository diagnosticRunRepository;
    private final PersistedDiagnosticFindingRepository findingRepository;

    public DiagnosticService(
            TelemetryRepository telemetryRepository,
            TelemetryAnalyzer telemetryAnalyzer,
            DiagnosticRunRepository diagnosticRunRepository,
            PersistedDiagnosticFindingRepository findingRepository
    ) {
        this.telemetryRepository = telemetryRepository;
        this.telemetryAnalyzer = telemetryAnalyzer;
        this.diagnosticRunRepository = diagnosticRunRepository;
        this.findingRepository = findingRepository;
    }

    public DiagnosticResponse runDiagnostics(Long telemetryReadingId) {

        TelemetryReading reading = telemetryRepository.findById(telemetryReadingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Telemetry reading not found with id: " + telemetryReadingId
                        )
                );

        List<DiagnosticFinding> findings =
                telemetryAnalyzer.analyze(reading);

        DiagnosticRun diagnosticRun = new DiagnosticRun(
                reading,
                DiagnosticStatus.COMPLETED,
                LocalDateTime.now()
        );

        DiagnosticRun savedRun =
                diagnosticRunRepository.save(diagnosticRun);

        List<PersistedDiagnosticFinding> persistedFindings =
                new ArrayList<>();

        for (DiagnosticFinding finding : findings) {

            PersistedDiagnosticFinding persistedFinding =
                    new PersistedDiagnosticFinding(
                            savedRun,
                            finding.getAnomalyType(),
                            finding.getMetric(),
                            finding.getObservedValue(),
                            finding.getExpectedValue(),
                            finding.getDeviationPct()
                    );

            persistedFindings.add(
                    findingRepository.save(persistedFinding)
            );
        }

        return new DiagnosticResponse(
                savedRun.getId(),
                savedRun.getStatus(),
                savedRun.getCreatedAt(),
                persistedFindings
        );
    }
}