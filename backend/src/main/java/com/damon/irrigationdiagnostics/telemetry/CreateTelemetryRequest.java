package com.damon.irrigationdiagnostics.telemetry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public class CreateTelemetryRequest {

    @NotNull
    private Long zoneId;

    @NotNull
    private LocalDateTime recordedAt;

    @PositiveOrZero
    private double flowLpm;

    @PositiveOrZero
    private double pressurePsi;

    @NotNull
    private ValveState valveState;

    @PositiveOrZero
    private int runtimeSeconds;

    @PositiveOrZero
    private double initialSoilMoisturePct;

    @PositiveOrZero
    private double finalSoilMoisturePct;

    private String errorCode;

    public CreateTelemetryRequest(
            Long zoneId,
            LocalDateTime recordedAt,
            double flowLpm,
            double pressurePsi,
            ValveState valveState,
            int runtimeSeconds,
            double initialSoilMoisturePct,
            double finalSoilMoisturePct,
            String errorCode
    ) {
        this.zoneId = zoneId;
        this.recordedAt = recordedAt;
        this.flowLpm = flowLpm;
        this.pressurePsi = pressurePsi;
        this.valveState = valveState;
        this.runtimeSeconds = runtimeSeconds;
        this.initialSoilMoisturePct = initialSoilMoisturePct;
        this.finalSoilMoisturePct = finalSoilMoisturePct;
        this.errorCode = errorCode;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public double getFlowLpm() {
        return flowLpm;
    }

    public double getPressurePsi() {
        return pressurePsi;
    }

    public ValveState getValveState() {
        return valveState;
    }

    public int getRuntimeSeconds() {
        return runtimeSeconds;
    }

    public double getInitialSoilMoisturePct() {
        return initialSoilMoisturePct;
    }

    public double getFinalSoilMoisturePct() {
        return finalSoilMoisturePct;
    }

    public String getErrorCode() {
        return errorCode;
    }
}