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
import com.damon.irrigationdiagnostics.inference.InferenceResult;

import com.damon.irrigationdiagnostics.inference.InferenceRun;
import com.damon.irrigationdiagnostics.inference.InferenceRunRepository;

import com.damon.irrigationdiagnostics.telemetry.TelemetryNotFoundException;

@Service
public class DiagnosticService {

    private final TelemetryRepository telemetryRepository;
    private final TelemetryAnalyzer telemetryAnalyzer;
    private final DiagnosticRunRepository diagnosticRunRepository;
    private final PersistedDiagnosticFindingRepository findingRepository;
    private final InferenceProvider inferenceProvider;
    private final InferenceRunRepository inferenceRunRepository;

    public DiagnosticService(
            TelemetryRepository telemetryRepository,
            TelemetryAnalyzer telemetryAnalyzer,
            DiagnosticRunRepository diagnosticRunRepository,
            PersistedDiagnosticFindingRepository findingRepository,
            InferenceProvider inferenceProvider,
            InferenceRunRepository inferenceRunRepository
    ) {
        this.telemetryRepository = telemetryRepository;
        this.telemetryAnalyzer = telemetryAnalyzer;
        this.diagnosticRunRepository = diagnosticRunRepository;
        this.findingRepository = findingRepository;
        this.inferenceProvider = inferenceProvider;
        this.inferenceRunRepository = inferenceRunRepository;
    }

    public DiagnosticResponse runDiagnostics(Long telemetryReadingId) {

        TelemetryReading reading = telemetryRepository.findById(telemetryReadingId)
                .orElseThrow(() -> new TelemetryNotFoundException(telemetryReadingId));

        // 1. Run deterministic analysis
        List<DiagnosticFinding> findings =
                telemetryAnalyzer.analyze(reading);

        // 2. Build the AI prompt only from deterministic findings
        String prompt = buildPrompt(findings);

        String aiExplanation;
        InferenceResult inferenceResult = null;
        DiagnosticStatus status = DiagnosticStatus.COMPLETED;
        boolean inferenceSuccess = true;
        String inferenceErrorMessage = null;

        try {
            inferenceResult = inferenceProvider.generateExplanation(prompt);
            aiExplanation = inferenceResult.getExplanation();
        } catch (Exception e) {
            aiExplanation =
                    "AI explanation unavailable. Deterministic diagnostic findings are still valid.";

            status = DiagnosticStatus.COMPLETED_WITHOUT_AI;
            inferenceSuccess = false;
            inferenceErrorMessage = e.getMessage();
        }

        // 4. Save the diagnostic run with the correct final status
        DiagnosticRun diagnosticRun = new DiagnosticRun(
                reading,
                status,
                LocalDateTime.now()
        );

        DiagnosticRun savedRun =
                diagnosticRunRepository.save(diagnosticRun);

        InferenceRun inferenceRun;

        if (inferenceSuccess && inferenceResult != null) {

            inferenceRun = new InferenceRun(
                    savedRun,
                    "qwen3.5:9b",
                    inferenceResult.getPromptTokens(),
                    inferenceResult.getOutputTokens(),
                    inferenceResult.getTotalLatencyMs(),
                    inferenceResult.getGenerationTokensPerSecond(),
                    true,
                    null,
                    LocalDateTime.now()
            );

        } else {

            inferenceRun = new InferenceRun(
                    savedRun,
                    "qwen3.5:9b",
                    0,
                    0,
                    0,
                    0.0,
                    false,
                    inferenceErrorMessage,
                    LocalDateTime.now()
            );
        }

        inferenceRunRepository.save(inferenceRun);

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

        int promptTokens = 0;
        int outputTokens = 0;
        long totalLatencyMs = 0;
        double generationTokensPerSecond = 0.0;

        if (inferenceResult != null) {
            promptTokens = inferenceResult.getPromptTokens();
            outputTokens = inferenceResult.getOutputTokens();
            totalLatencyMs = inferenceResult.getTotalLatencyMs();
            generationTokensPerSecond =
                    inferenceResult.getGenerationTokensPerSecond();
        }
        // 6. Return everything
        return new DiagnosticResponse(
                savedRun.getId(),
                savedRun.getStatus(),
                savedRun.getCreatedAt(),
                persistedFindings,
                aiExplanation,
                promptTokens,
                outputTokens,
                totalLatencyMs,
                generationTokensPerSecond
        );
    }

    private String buildPrompt(List<DiagnosticFinding> findings) {
        if (findings.isEmpty()) {
            return """
                You are explaining deterministic irrigation diagnostic findings.

                No deterministic anomalies were detected.

                Rules:
                - State that no anomalies were detected by the deterministic rules.
                - Do not invent sensor readings, conditions, faults, or causes.
                - Do not suggest that a hidden problem was detected.
                - Keep the response concise.
                """;
        }

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
        You are explaining deterministic irrigation diagnostic findings.

        The anomaly classifications and numerical values below were already
        determined by deterministic Java rules. Treat them as verified facts.

        Rules:
        - Do not add, remove, rename, or reinterpret anomaly classifications.
        - Do not invent sensor readings, device states, error codes, environmental
          conditions, or system conditions.
        - Do not state or imply that a root cause has been identified.
        - Do not claim that a leak, blockage, valve problem, sensor failure,
          hydraulic issue, soil condition, runoff, or any other physical cause
          has been confirmed.
        - Possible causes may only be mentioned as hypotheses requiring verification.
        - Do not say that a hypothesis "explains" a finding.
        - Do not use phrases such as "explains", "accounts for", "causes",
          "contributes to", or similar language that connects a hypothesis
          directly to a verified finding.
        - Investigation steps must be framed as checks to gather more evidence,
          not as diagnoses.
        - Phrase investigation steps as neutral verification actions.
          Example: "Inspect valve operation and record whether abnormal behavior is present."
        - First summarize only the verified findings.
        - Then provide a short list of reasonable investigation steps.
        - Keep the response concise.

        Verified findings:
        """);

        for (DiagnosticFinding finding : findings) {
            prompt.append(String.format(
                    "%s | metric=%s | observed=%.2f | expected=%.2f | deviationPct=%.2f%n",
                    finding.getAnomalyType(),
                    finding.getMetric(),
                    finding.getObservedValue(),
                    finding.getExpectedValue(),
                    finding.getDeviationPct()
            ));
        }

        return prompt.toString();
    }
}