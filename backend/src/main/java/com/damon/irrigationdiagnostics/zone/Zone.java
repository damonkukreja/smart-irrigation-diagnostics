package com.damon.irrigationdiagnostics.zone;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double expectedFlowLpm;
    private double expectedPressurePsi;
    private double minimumMoistureIncreasePct;

    protected Zone() {
    }

    public Zone(
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