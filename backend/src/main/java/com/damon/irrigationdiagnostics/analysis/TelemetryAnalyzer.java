package com.damon.irrigationdiagnostics.analysis;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelemetryAnalyzer {

    private final List<AnomalyRule> rules;

    public TelemetryAnalyzer(List<AnomalyRule> rules) {
        this.rules = rules;
    }

    public List<DiagnosticFinding> analyze(TelemetryReading reading) {

        List<DiagnosticFinding> findings = new ArrayList<>();

        for (AnomalyRule rule : rules) {
            rule.evaluate(reading)
                    .ifPresent(findings::add);
        }

        return findings;
    }
}