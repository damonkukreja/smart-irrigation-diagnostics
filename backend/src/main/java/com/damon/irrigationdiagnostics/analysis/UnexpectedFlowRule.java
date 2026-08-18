package com.damon.irrigationdiagnostics.analysis;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import com.damon.irrigationdiagnostics.telemetry.ValveState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UnexpectedFlowRule implements AnomalyRule {

    private static final double FLOW_THRESHOLD_LPM = 1.0;

    @Override
    public Optional<DiagnosticFinding> evaluate(TelemetryReading reading) {

        if (reading.getValveState() != ValveState.CLOSED) {
            return Optional.empty();
        }

        double observedFlow = reading.getFlowLpm();

        if (observedFlow > FLOW_THRESHOLD_LPM) {

            DiagnosticFinding finding = new DiagnosticFinding(
                    AnomalyType.UNEXPECTED_FLOW,
                    "flowLpm",
                    observedFlow,
                    0.0,
                    0.0
            );

            return Optional.of(finding);
        }

        return Optional.empty();
    }
}