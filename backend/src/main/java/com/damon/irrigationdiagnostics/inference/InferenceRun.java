package com.damon.irrigationdiagnostics.inference;

import com.damon.irrigationdiagnostics.diagnostic.DiagnosticRun;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class InferenceRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private DiagnosticRun diagnosticRun;

    private String model;

    private int promptTokens;

    private int outputTokens;

    private long totalLatencyMs;

    private double generationTokensPerSecond;

    private boolean success;

    private String errorMessage;

    private LocalDateTime createdAt;

    protected InferenceRun() {
    }

    public InferenceRun(
            DiagnosticRun diagnosticRun,
            String model,
            int promptTokens,
            int outputTokens,
            long totalLatencyMs,
            double generationTokensPerSecond,
            boolean success,
            String errorMessage,
            LocalDateTime createdAt
    ) {
        this.diagnosticRun = diagnosticRun;
        this.model = model;
        this.promptTokens = promptTokens;
        this.outputTokens = outputTokens;
        this.totalLatencyMs = totalLatencyMs;
        this.generationTokensPerSecond = generationTokensPerSecond;
        this.success = success;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public DiagnosticRun getDiagnosticRun() {
        return diagnosticRun;
    }

    public String getModel() {
        return model;
    }

    public int getPromptTokens() {
        return promptTokens;
    }

    public int getOutputTokens() {
        return outputTokens;
    }

    public long getTotalLatencyMs() {
        return totalLatencyMs;
    }

    public double getGenerationTokensPerSecond() {
        return generationTokensPerSecond;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}