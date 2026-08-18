package com.damon.irrigationdiagnostics.inference;

public interface InferenceProvider {

    InferenceResult generateExplanation(String prompt);
}