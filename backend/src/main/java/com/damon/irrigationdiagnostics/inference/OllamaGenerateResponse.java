package com.damon.irrigationdiagnostics.inference;

public record OllamaGenerateResponse(
        String response,
        Integer prompt_eval_count,
        Integer eval_count,
        Long total_duration,
        Long eval_duration
) {
}