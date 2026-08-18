package com.damon.irrigationdiagnostics.analysis;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;

import java.util.Optional;

public interface AnomalyRule {

    Optional<DiagnosticFinding> evaluate(TelemetryReading reading);
}