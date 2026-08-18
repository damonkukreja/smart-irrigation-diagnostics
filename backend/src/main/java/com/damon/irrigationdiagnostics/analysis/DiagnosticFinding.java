package com.damon.irrigationdiagnostics.analysis;

public class DiagnosticFinding {

    private final AnomalyType anomalyType;
    private final String metric;
    private final double observedValue;
    private final double expectedValue;
    private final double deviationPct;

    public DiagnosticFinding(
            AnomalyType anomalyType,
            String metric,
            double observedValue,
            double expectedValue,
            double deviationPct
    ) {
        this.anomalyType = anomalyType;
        this.metric = metric;
        this.observedValue = observedValue;
        this.expectedValue = expectedValue;
        this.deviationPct = deviationPct;
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