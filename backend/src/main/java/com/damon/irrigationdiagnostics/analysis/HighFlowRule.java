package com.damon.irrigationdiagnostics.analysis;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import org.springframework.stereotype.Component;

import java.util.Optional;

import com.damon.irrigationdiagnostics.telemetry.ValveState;

@Component
public class HighFlowRule implements AnomalyRule {

    private static final double HIGH_FLOW_MULTIPLIER = 1.20;

    @Override
    public Optional<DiagnosticFinding> evaluate(TelemetryReading reading) {

        if (reading.getValveState() != ValveState.OPEN) {
            return Optional.empty();
        }

        double expectedFlow = reading.getZone().getExpectedFlowLpm();
        double observedFlow = reading.getFlowLpm();

        double threshold = expectedFlow * HIGH_FLOW_MULTIPLIER;

        if (observedFlow > threshold) {

            double deviationPct =
                    ((observedFlow - expectedFlow) / expectedFlow) * 100.0;

            DiagnosticFinding finding = new DiagnosticFinding(
                    AnomalyType.HIGH_FLOW,
                    "flowLpm",
                    observedFlow,
                    expectedFlow,
                    deviationPct
            );

            return Optional.of(finding);
        }

        return Optional.empty();
    }
}