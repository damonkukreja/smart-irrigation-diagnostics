package com.damon.irrigationdiagnostics.diagnostic;

import java.time.LocalDateTime;
import java.util.List;

public class DiagnosticResponse {

    private final Long diagnosticRunId;
    private final DiagnosticStatus status;
    private final LocalDateTime createdAt;
    private final List<PersistedDiagnosticFinding> findings;

    public DiagnosticResponse(
            Long diagnosticRunId,
            DiagnosticStatus status,
            LocalDateTime createdAt,
            List<PersistedDiagnosticFinding> findings
    ) {
        this.diagnosticRunId = diagnosticRunId;
        this.status = status;
        this.createdAt = createdAt;
        this.findings = findings;
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
}