package com.damon.irrigationdiagnostics.inference;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OllamaInferenceProvider implements InferenceProvider {

    private final RestClient restClient;

    public OllamaInferenceProvider() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    @Override
    public InferenceResult generateExplanation(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "model", "qwen3.5:9b",
                "prompt", prompt,
                "stream", false,
                "think", false
        );

        OllamaGenerateResponse response = restClient.post()
                .uri("/api/generate")
                .body(requestBody)
                .retrieve()
                .body(OllamaGenerateResponse.class);

        if (response == null) {
            throw new RuntimeException("Ollama returned an empty response");
        }

        long totalLatencyMs =
                response.total_duration() != null
                        ? response.total_duration() / 1_000_000
                        : 0;

        double generationTokensPerSecond = 0.0;

        if (response.eval_duration() != null
                && response.eval_duration() > 0
                && response.eval_count() != null) {

            double generationSeconds =
                    response.eval_duration() / 1_000_000_000.0;

            generationTokensPerSecond =
                    response.eval_count() / generationSeconds;
        }

        return new InferenceResult(
                response.response(),
                response.prompt_eval_count() != null ? response.prompt_eval_count() : 0,
                response.eval_count() != null ? response.eval_count() : 0,
                totalLatencyMs,
                generationTokensPerSecond
        );
    }
}