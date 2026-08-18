package com.damon.irrigationdiagnostics.diagnostic;

import java.time.LocalDateTime;
import java.util.List;

public class DiagnosticResponse {

    private final Long diagnosticRunId;
    private final DiagnosticStatus status;
    private final LocalDateTime createdAt;
    private final List<PersistedDiagnosticFinding> findings;
    private final String aiExplanation;

    private final Integer promptTokens;
    private final Integer outputTokens;
    private final Long totalLatencyMs;
    private final Double generationTokensPerSecond;

    public DiagnosticResponse(
            Long diagnosticRunId,
            DiagnosticStatus status,
            LocalDateTime createdAt,
            List<PersistedDiagnosticFinding> findings,
            String aiExplanation,
            Integer promptTokens,
            Integer outputTokens,
            Long totalLatencyMs,
            Double generationTokensPerSecond
    ) {
        this.diagnosticRunId = diagnosticRunId;
        this.status = status;
        this.createdAt = createdAt;
        this.findings = findings;
        this.aiExplanation = aiExplanation;
        this.promptTokens = promptTokens;
        this.outputTokens = outputTokens;
        this.totalLatencyMs = totalLatencyMs;
        this.generationTokensPerSecond = generationTokensPerSecond;
    }

    public Long getDiagnosticRunId() {
        return diagnosticRunId;
    }

    public DiagnosticStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<PersistedDiagnosticFinding> getFindings() {
        return findings;
    }
    public String getAiExplanation() {
        return aiExplanation;
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public Integer getOutputTokens() {
        return outputTokens;
    }

    public Long getTotalLatencyMs() {
        return totalLatencyMs;
    }

    public Double getGenerationTokensPerSecond() {
        return generationTokensPerSecond;
    }
}