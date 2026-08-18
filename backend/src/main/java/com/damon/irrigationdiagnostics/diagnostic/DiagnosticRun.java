package com.damon.irrigationdiagnostics.diagnostic;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DiagnosticRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TelemetryReading telemetryReading;

    @Enumerated(EnumType.STRING)
    private DiagnosticStatus status;

    private LocalDateTime createdAt;

    protected DiagnosticRun() {
    }

    public DiagnosticRun(
            TelemetryReading telemetryReading,
            DiagnosticStatus status,
            LocalDateTime createdAt
    ) {
        this.telemetryReading = telemetryReading;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public TelemetryReading getTelemetryReading() {
        return telemetryReading;
    }

    public DiagnosticStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}