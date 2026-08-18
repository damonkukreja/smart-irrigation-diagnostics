package com.damon.irrigationdiagnostics.analysis;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import org.springframework.stereotype.Component;

import java.util.Optional;

import com.damon.irrigationdiagnostics.telemetry.ValveState;

@Component
public class LowFlowRule implements AnomalyRule {

    private static final double LOW_FLOW_MULTIPLIER = 0.70;

    @Override
    public Optional<DiagnosticFinding> evaluate(TelemetryReading reading) {

        if (reading.getValveState() != ValveState.OPEN) {
            return Optional.empty();
        }

        double expectedFlow = reading.getZone().getExpectedFlowLpm();
        double observedFlow = reading.getFlowLpm();

        double threshold = expectedFlow * LOW_FLOW_MULTIPLIER;

        if (observedFlow < threshold) {

            double deviationPct =
                    ((observedFlow - expectedFlow) / expectedFlow) * 100.0;

            DiagnosticFinding finding = new DiagnosticFinding(
                    AnomalyType.LOW_FLOW,
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