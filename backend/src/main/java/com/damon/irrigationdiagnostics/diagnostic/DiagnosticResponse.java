package com.damon.irrigationdiagnostics.diagnostic;

import java.time.LocalDateTime;
import java.util.List;

public class DiagnosticResponse {

    private final Long diagnosticRunId;
    private final DiagnosticStatus status;
    private final LocalDateTime createdAt;
    private final List<PersistedDiagnosticFinding> findings;
    private final String aiExplanation;

    public DiagnosticResponse(
            Long diagnosticRunId,
            DiagnosticStatus status,
            LocalDateTime createdAt,
            List<PersistedDiagnosticFinding> findings,
            String aiExplanation
    ) {
        this.diagnosticRunId = diagnosticRunId;
        this.status = status;
        this.createdAt = createdAt;
        this.findings = findings;
        this.aiExplanation = aiExplanation;
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
}