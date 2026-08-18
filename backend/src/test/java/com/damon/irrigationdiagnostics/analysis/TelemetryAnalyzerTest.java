package com.damon.irrigationdiagnostics.analysis;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import com.damon.irrigationdiagnostics.telemetry.ValveState;
import com.damon.irrigationdiagnostics.zone.Zone;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TelemetryAnalyzerTest {

    @Test
    void normalReadingProducesNoFindings() {

        Zone zone = new Zone(
                "North Lawn",
                13.0,
                54.0,
                3.0
        );

        TelemetryReading reading = new TelemetryReading(
                zone,
                LocalDateTime.now(),
                13.4,
                53.5,
                ValveState.OPEN,
                600,
                22.0,
                26.0,
                null
        );

        TelemetryAnalyzer analyzer = new TelemetryAnalyzer(
                List.of(
                        new HighFlowRule(),
                        new LowFlowRule(),
                        new PressureDropRule(),
                        new LowMoistureResponseRule(),
                        new UnexpectedFlowRule()
                )
        );

        List<DiagnosticFinding> findings = analyzer.analyze(reading);

        assertTrue(findings.isEmpty());
    }

    @Test
    void abnormalReadingProducesMultipleFindings() {

        Zone zone = new Zone(
                "North Lawn",
                13.0,
                54.0,
                3.0
        );

        TelemetryReading reading = new TelemetryReading(
                zone,
                LocalDateTime.now(),
                17.0,
                40.0,
                ValveState.OPEN,
                600,
                22.0,
                23.0,
                null
        );

        TelemetryAnalyzer analyzer = new TelemetryAnalyzer(
                List.of(
                        new HighFlowRule(),
                        new LowFlowRule(),
                        new PressureDropRule(),
                        new LowMoistureResponseRule(),
                        new UnexpectedFlowRule()
                )
        );

        List<DiagnosticFinding> findings = analyzer.analyze(reading);

        assertTrue(
                findings.stream()
                        .anyMatch(f -> f.getAnomalyType() == AnomalyType.HIGH_FLOW)
        );

        assertTrue(
                findings.stream()
                        .anyMatch(f -> f.getAnomalyType() == AnomalyType.PRESSURE_DROP)
        );

        assertTrue(
                findings.stream()
                        .anyMatch(f -> f.getAnomalyType() == AnomalyType.LOW_MOISTURE_RESPONSE)
        );
    }
    @Test
    void closedValveWithFlowProducesUnexpectedFlowFinding() {

        Zone zone = new Zone(
                "North Lawn",
                13.0,
                54.0,
                3.0
        );

        TelemetryReading reading = new TelemetryReading(
                zone,
                LocalDateTime.now(),
                2.5,
                54.0,
                ValveState.CLOSED,
                0,
                22.0,
                22.0,
                null
        );

        TelemetryAnalyzer analyzer = new TelemetryAnalyzer(
                List.of(
                        new HighFlowRule(),
                        new LowFlowRule(),
                        new PressureDropRule(),
                        new LowMoistureResponseRule(),
                        new UnexpectedFlowRule()
                )
        );

        List<DiagnosticFinding> findings = analyzer.analyze(reading);

        assertTrue(
                findings.stream()
                        .anyMatch(f -> f.getAnomalyType() == AnomalyType.UNEXPECTED_FLOW)
        );
    }

}