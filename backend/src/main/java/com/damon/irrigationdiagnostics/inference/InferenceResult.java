package com.damon.irrigationdiagnostics.inference;

public class InferenceResult {

    private final String explanation;
    private final int promptTokens;
    private final int outputTokens;
    private final long totalLatencyMs;
    private final double generationTokensPerSecond;

    public InferenceResult(
            String explanation,
            int promptTokens,
            int outputTokens,
            long totalLatencyMs,
            double generationTokensPerSecond
    ) {
        this.explanation = explanation;
        this.promptTokens = promptTokens;
        this.outputTokens = outputTokens;
        this.totalLatencyMs = totalLatencyMs;
        this.generationTokensPerSecond = generationTokensPerSecond;
    }

    public String getExplanation() {
        return explanation;
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
}