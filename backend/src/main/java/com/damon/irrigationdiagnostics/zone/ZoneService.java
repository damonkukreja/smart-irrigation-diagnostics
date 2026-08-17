package com.damon.irrigationdiagnostics.zone;

import org.springframework.stereotype.Service;

@Service
public class ZoneService {

    public Zone createZone(CreateZoneRequest request) {
        return new Zone(
                1L,
                request.getName(),
                request.getExpectedFlowLpm(),
                request.getExpectedPressurePsi(),
                request.getMinimumMoistureIncreasePct()
        );
    }
}