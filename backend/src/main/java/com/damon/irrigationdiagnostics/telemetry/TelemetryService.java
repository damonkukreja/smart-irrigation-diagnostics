package com.damon.irrigationdiagnostics.telemetry;

import com.damon.irrigationdiagnostics.zone.Zone;
import com.damon.irrigationdiagnostics.zone.ZoneNotFoundException;
import com.damon.irrigationdiagnostics.zone.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final ZoneRepository zoneRepository;

    public TelemetryService(
            TelemetryRepository telemetryRepository,
            ZoneRepository zoneRepository
    ) {
        this.telemetryRepository = telemetryRepository;
        this.zoneRepository = zoneRepository;
    }

    public TelemetryReading createTelemetry(CreateTelemetryRequest request) {

        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new ZoneNotFoundException(request.getZoneId()));

        TelemetryReading reading = new TelemetryReading(
                zone,
                request.getRecordedAt(),
                request.getFlowLpm(),
                request.getPressurePsi(),
                request.getValveState(),
                request.getRuntimeSeconds(),
                request.getInitialSoilMoisturePct(),
                request.getFinalSoilMoisturePct(),
                request.getErrorCode()
        );

        return telemetryRepository.save(reading);
    }

    public List<TelemetryReading> getTelemetryForZone(Long zoneId) {

        if (!zoneRepository.existsById(zoneId)) {
            throw new ZoneNotFoundException(zoneId);
        }

        return telemetryRepository.findByZoneId(zoneId);
    }
}