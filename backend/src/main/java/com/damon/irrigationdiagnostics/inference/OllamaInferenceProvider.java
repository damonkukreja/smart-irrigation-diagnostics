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
    public String generateExplanation(String prompt) {

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

        return response.response();
    }
}