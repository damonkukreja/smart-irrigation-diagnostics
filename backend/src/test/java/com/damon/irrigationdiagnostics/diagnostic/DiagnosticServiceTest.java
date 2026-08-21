package com.damon.irrigationdiagnostics.diagnostic;

import com.damon.irrigationdiagnostics.analysis.AnomalyType;
import com.damon.irrigationdiagnostics.analysis.DiagnosticFinding;
import com.damon.irrigationdiagnostics.analysis.TelemetryAnalyzer;
import com.damon.irrigationdiagnostics.inference.InferenceProvider;
import com.damon.irrigationdiagnostics.inference.InferenceRunRepository;
import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import com.damon.irrigationdiagnostics.telemetry.TelemetryRepository;
import com.damon.irrigationdiagnostics.telemetry.ValveState;
import com.damon.irrigationdiagnostics.zone.Zone;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DiagnosticServiceTest {

    @Test
    void aiFailurePreservesDeterministicFindings() {

        TelemetryRepository telemetryRepository = mock(TelemetryRepository.class);
        TelemetryAnalyzer telemetryAnalyzer = mock(TelemetryAnalyzer.class);
        DiagnosticRunRepository diagnosticRunRepository = mock(DiagnosticRunRepository.class);
        PersistedDiagnosticFindingRepository findingRepository =
                mock(PersistedDiagnosticFindingRepository.class);
        InferenceProvider inferenceProvider = mock(InferenceProvider.class);
        InferenceRunRepository inferenceRunRepository = mock(InferenceRunRepository.class);

        DiagnosticService diagnosticService = new DiagnosticService(
                telemetryRepository,
                telemetryAnalyzer,
                diagnosticRunRepository,
                findingRepository,
                inferenceProvider,
                inferenceRunRepository
        );

        Zone zone = new Zone(
                "North Lawn",
                13.0,
                54.0,
                3.0
        );

        TelemetryReading reading = new TelemetryReading(
                zone,
                LocalDateTime.of(2026, 8, 17, 18, 55),
                17.0,
                40.0,
                ValveState.OPEN,
                600,
                22.0,
                23.0,
                null
        );

        List<DiagnosticFinding> findings = List.of(
                new DiagnosticFinding(
                        AnomalyType.HIGH_FLOW,
                        "flowLpm",
                        17.0,
                        13.0,
                        30.77
                )
        );

        when(telemetryRepository.findById(2L))
                .thenReturn(Optional.of(reading));

        when(telemetryAnalyzer.analyze(reading))
                .thenReturn(findings);

        when(inferenceProvider.generateExplanation(anyString()))
                .thenThrow(new RuntimeException("Ollama unavailable"));

        when(diagnosticRunRepository.save(any(DiagnosticRun.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(findingRepository.save(any(PersistedDiagnosticFinding.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(inferenceRunRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DiagnosticResponse response =
                diagnosticService.runDiagnostics(2L);

        assertEquals(
                DiagnosticStatus.COMPLETED_WITHOUT_AI,
                response.getStatus()
        );

        assertEquals(1, response.getFindings().size());

        assertEquals(
                AnomalyType.HIGH_FLOW,
                response.getFindings().get(0).getAnomalyType()
        );

        assertTrue(
                response.getAiExplanation()
                        .contains("AI explanation unavailable")
        );

        assertEquals(0, response.getPromptTokens());
        assertEquals(0, response.getOutputTokens());

        verify(inferenceProvider).generateExplanation(anyString());
        verify(findingRepository).save(any(PersistedDiagnosticFinding.class));
    }
}