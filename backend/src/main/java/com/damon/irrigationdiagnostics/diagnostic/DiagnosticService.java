package com.damon.irrigationdiagnostics.diagnostic;

import com.damon.irrigationdiagnostics.analysis.DiagnosticFinding;
import com.damon.irrigationdiagnostics.analysis.TelemetryAnalyzer;
import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import com.damon.irrigationdiagnostics.telemetry.TelemetryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.damon.irrigationdiagnostics.inference.InferenceProvider;

@Service
public class DiagnosticService {

    private final TelemetryRepository telemetryRepository;
    private final TelemetryAnalyzer telemetryAnalyzer;
    private final DiagnosticRunRepository diagnosticRunRepository;
    private final PersistedDiagnosticFindingRepository findingRepository;
    private final InferenceProvider inferenceProvider;

    public DiagnosticService(
            TelemetryRepository telemetryRepository,
            TelemetryAnalyzer telemetryAnalyzer,
            DiagnosticRunRepository diagnosticRunRepository,
            PersistedDiagnosticFindingRepository findingRepository,
            InferenceProvider inferenceProvider
    ) {
        this.telemetryRepository = telemetryRepository;
        this.telemetryAnalyzer = telemetryAnalyzer;
        this.diagnosticRunRepository = diagnosticRunRepository;
        this.findingRepository = findingRepository;
        this.inferenceProvider = inferenceProvider;
    }

    public DiagnosticResponse runDiagnostics(Long telemetryReadingId) {

        TelemetryReading reading = telemetryRepository.findById(telemetryReadingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Telemetry reading not found with id: " + telemetryReadingId
                        )
                );

        // 1. Run deterministic analysis
        List<DiagnosticFinding> findings =
                telemetryAnalyzer.analyze(reading);

        // 2. Build the AI prompt only from deterministic findings
        String prompt = buildPrompt(findings);

        String aiExplanation;
        DiagnosticStatus status = DiagnosticStatus.COMPLETED;

        // 3. Attempt AI explanation
        try {
            aiExplanation = inferenceProvider.generateExplanation(prompt);
        } catch (Exception e) {
            aiExplanation =
                    "AI explanation unavailable. Deterministic diagnostic findings are still valid.";

            status = DiagnosticStatus.COMPLETED_WITHOUT_AI;
        }

        // 4. Save the diagnostic run with the correct final status
        DiagnosticRun diagnosticRun = new DiagnosticRun(
                reading,
                status,
                LocalDateTime.now()
        );

        DiagnosticRun savedRun =
                diagnosticRunRepository.save(diagnosticRun);

        // 5. Persist each deterministic finding
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

        // 6. Return everything
        return new DiagnosticResponse(
                savedRun.getId(),
                savedRun.getStatus(),
                savedRun.getCreatedAt(),
                persistedFindings,
                aiExplanation
        );
    }

    private String buildPrompt(List<DiagnosticFinding> findings) {

        if (findings.isEmpty()) {
            return """
                No deterministic irrigation anomalies were detected.
                Explain this result briefly.
                Do not invent problems or sensor values.
                """;
        }

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
        You are explaining deterministic irrigation diagnostic findings.

        The anomaly classifications and numerical values below were already
        determined by deterministic Java rules. Treat them as verified facts.

        Rules:
        - Do not add, remove, or reinterpret anomaly classifications.
        - Do not invent sensor readings, device states, error codes, or system conditions.
        - Do not state a root cause as fact.
        - Do not claim that a leak, blockage, sensor failure, runoff, soil condition,
          or other physical cause has been confirmed.
        - If mentioning possible causes, clearly label them as hypotheses that require verification.
        - Keep the explanation concise.
        - First summarize the verified findings.
        - Then provide a short list of reasonable investigation steps.

        Findings:
        """);

        for (DiagnosticFinding finding : findings) {
            prompt.append("\n- ")
                    .append(finding.getAnomalyType())
                    .append(": ")
                    .append(finding.getMetric())
                    .append(" observed=")
                    .append(finding.getObservedValue())
                    .append(", expected=")
                    .append(finding.getExpectedValue())
                    .append(", deviationPct=")
                    .append(finding.getDeviationPct());
        }

        return prompt.toString();
    }
}