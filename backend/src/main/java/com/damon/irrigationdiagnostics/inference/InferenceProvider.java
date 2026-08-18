package com.damon.irrigationdiagnostics.inference;

public interface InferenceProvider {

    String generateExplanation(String prompt);
}