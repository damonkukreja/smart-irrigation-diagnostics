package com.damon.irrigationdiagnostics.zone;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public Zone createZone(CreateZoneRequest request) {

        Zone zone = new Zone(
                request.getName(),
                request.getExpectedFlowLpm(),
                request.getExpectedPressurePsi(),
                request.getMinimumMoistureIncreasePct()
        );

        return zoneRepository.save(zone);
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ZoneNotFoundException(id));
    }
}