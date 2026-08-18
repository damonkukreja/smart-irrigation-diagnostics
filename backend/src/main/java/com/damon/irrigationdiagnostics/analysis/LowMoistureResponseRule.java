package com.damon.irrigationdiagnostics.analysis;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import com.damon.irrigationdiagnostics.telemetry.ValveState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LowMoistureResponseRule implements AnomalyRule {

    private static final int MIN_RUNTIME_SECONDS = 300;

    @Override
    public Optional<DiagnosticFinding> evaluate(TelemetryReading reading) {

        if (reading.getValveState() != ValveState.OPEN) {
            return Optional.empty();
        }

        if (reading.getRuntimeSeconds() < MIN_RUNTIME_SECONDS) {
            return Optional.empty();
        }

        double moistureIncrease =
                reading.getFinalSoilMoisturePct()
                        - reading.getInitialSoilMoisturePct();

        double expectedIncrease =
                reading.getZone().getMinimumMoistureIncreasePct();

        if (moistureIncrease < expectedIncrease) {

            double deviationPct =
                    ((moistureIncrease - expectedIncrease) / expectedIncrease) * 100.0;

            DiagnosticFinding finding = new DiagnosticFinding(
                    AnomalyType.LOW_MOISTURE_RESPONSE,
                    "soilMoistureIncreasePct",
                    moistureIncrease,
                    expectedIncrease,
                    deviationPct
            );

            return Optional.of(finding);
        }

        return Optional.empty();
    }
}