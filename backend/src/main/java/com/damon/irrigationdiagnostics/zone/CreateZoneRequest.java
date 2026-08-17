package com.damon.irrigationdiagnostics.zone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CreateZoneRequest {

    @NotBlank
    private String name;

    @Positive
    private double expectedFlowLpm;

    @Positive
    private double expectedPressurePsi;

    @Positive
    private double minimumMoistureIncreasePct;

    public CreateZoneRequest(
            String name,
            double expectedFlowLpm,
            double expectedPressurePsi,
            double minimumMoistureIncreasePct
    ) {
        this.name = name;
        this.expectedFlowLpm = expectedFlowLpm;
        this.expectedPressurePsi = expectedPressurePsi;
        this.minimumMoistureIncreasePct = minimumMoistureIncreasePct;
    }

    public String getName() {
        return name;
    }

    public double getExpectedFlowLpm() {
        return expectedFlowLpm;
    }

    public double getExpectedPressurePsi() {
        return expectedPressurePsi;
    }

    public double getMinimumMoistureIncreasePct() {
        return minimumMoistureIncreasePct;
    }
}