package com.damon.irrigationdiagnostics.diagnostic;

import com.damon.irrigationdiagnostics.analysis.AnomalyType;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class PersistedDiagnosticFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private DiagnosticRun diagnosticRun;

    @Enumerated(EnumType.STRING)
    private AnomalyType anomalyType;

    private String metric;

    private double observedValue;

    private double expectedValue;

    private double deviationPct;

    protected PersistedDiagnosticFinding() {
    }

    public PersistedDiagnosticFinding(
            DiagnosticRun diagnosticRun,
            AnomalyType anomalyType,
            String metric,
            double observedValue,
            double expectedValue,
            double deviationPct
    ) {
        this.diagnosticRun = diagnosticRun;
        this.anomalyType = anomalyType;
        this.metric = metric;
        this.observedValue = observedValue;
        this.expectedValue = expectedValue;
        this.deviationPct = deviationPct;
    }

    public Long getId() {
        return id;
    }

    public DiagnosticRun getDiagnosticRun() {
        return diagnosticRun;
    }

    public AnomalyType getAnomalyType() {
        return anomalyType;
    }

    public String getMetric() {
        return metric;
    }

    public double getObservedValue() {
        return observedValue;
    }

    public double getExpectedValue() {
        return expectedValue;
    }

    public double getDeviationPct() {
        return deviationPct;
    }
}