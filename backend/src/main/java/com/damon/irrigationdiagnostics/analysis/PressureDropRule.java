package com.damon.irrigationdiagnostics.analysis;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import com.damon.irrigationdiagnostics.telemetry.ValveState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PressureDropRule implements AnomalyRule {

    private static final double PRESSURE_MULTIPLIER = 0.80;

    @Override
    public Optional<DiagnosticFinding> evaluate(TelemetryReading reading) {

        if (reading.getValveState() != ValveState.OPEN) {
            return Optional.empty();
        }

        double expectedPressure = reading.getZone().getExpectedPressurePsi();
        double observedPressure = reading.getPressurePsi();

        double threshold = expectedPressure * PRESSURE_MULTIPLIER;

        if (observedPressure < threshold) {

            double deviationPct =
                    ((observedPressure - expectedPressure) / expectedPressure) * 100.0;

            DiagnosticFinding finding = new DiagnosticFinding(
                    AnomalyType.PRESSURE_DROP,
                    "pressurePsi",
                    observedPressure,
                    expectedPressure,
                    deviationPct
            );

            return Optional.of(finding);
        }

        return Optional.empty();
    }
}