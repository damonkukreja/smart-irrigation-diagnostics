package com.damon.irrigationdiagnostics.diagnostic;

import com.damon.irrigationdiagnostics.telemetry.TelemetryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiagnosticController.class)
class DiagnosticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnosticService diagnosticService;

    @Test
    void missingTelemetryReturns404() throws Exception {

        when(diagnosticService.runDiagnostics(999L))
                .thenThrow(new TelemetryNotFoundException(999L));

        mockMvc.perform(
                post("/api/diagnostics/telemetry/999")
        ).andExpect(
                status().isNotFound()
        );
    }
}