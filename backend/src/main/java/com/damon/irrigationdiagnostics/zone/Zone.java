package com.damon.irrigationdiagnostics.zone;

public class Zone {

    private Long id;
    private String name;
    private double expectedFlowLpm;
    private double expectedPressurePsi;
    private double minimumMoistureIncreasePct;


    public Zone(
            Long id,
            String name,
            double expectedFlowLpm,
            double expectedPressurePsi,
            double minimumMoistureIncreasePct
    ) {
        this.id = id;
        this.name = name;
        this.expectedFlowLpm = expectedFlowLpm;
        this.expectedPressurePsi = expectedPressurePsi;
        this.minimumMoistureIncreasePct = minimumMoistureIncreasePct;
    }

    public Long getId() {
        return id;
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