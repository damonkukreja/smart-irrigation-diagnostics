package com.damon.irrigationdiagnostics.telemetry;

import com.damon.irrigationdiagnostics.zone.Zone;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TelemetryReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Zone zone;

    private LocalDateTime recordedAt;

    private double flowLpm;

    private double pressurePsi;

    @Enumerated(EnumType.STRING)
    private ValveState valveState;

    private int runtimeSeconds;

    private double initialSoilMoisturePct;

    private double finalSoilMoisturePct;

    private String errorCode;

    protected TelemetryReading() {
    }

    public TelemetryReading(
            Zone zone,
            LocalDateTime recordedAt,
            double flowLpm,
            double pressurePsi,
            ValveState valveState,
            int runtimeSeconds,
            double initialSoilMoisturePct,
            double finalSoilMoisturePct,
            String errorCode
    ) {
        this.zone = zone;
        this.recordedAt = recordedAt;
        this.flowLpm = flowLpm;
        this.pressurePsi = pressurePsi;
        this.valveState = valveState;
        this.runtimeSeconds = runtimeSeconds;
        this.initialSoilMoisturePct = initialSoilMoisturePct;
        this.finalSoilMoisturePct = finalSoilMoisturePct;
        this.errorCode = errorCode;
    }

    public Long getId() {
        return id;
    }

    public Zone getZone() {
        return zone;
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