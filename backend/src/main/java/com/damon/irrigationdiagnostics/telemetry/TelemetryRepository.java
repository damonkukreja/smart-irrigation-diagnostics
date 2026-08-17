package com.damon.irrigationdiagnostics.telemetry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelemetryRepository extends JpaRepository<TelemetryReading, Long> {

    List<TelemetryReading> findByZoneId(Long zoneId);
}