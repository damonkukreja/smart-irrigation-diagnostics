package com.damon.irrigationdiagnostics.config;

import com.damon.irrigationdiagnostics.telemetry.TelemetryReading;
import com.damon.irrigationdiagnostics.telemetry.TelemetryRepository;
import com.damon.irrigationdiagnostics.telemetry.ValveState;
import com.damon.irrigationdiagnostics.zone.Zone;
import com.damon.irrigationdiagnostics.zone.ZoneRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DemoDataInitializer {

    private static final Logger logger =
            LoggerFactory.getLogger(DemoDataInitializer.class);

    @Bean
    CommandLineRunner seedDemoData(
            ZoneRepository zoneRepository,
            TelemetryRepository telemetryRepository) {

        return args -> {
            if (zoneRepository.count() > 0) {
                logger.info("Demo data already present; skipping initialization.");
                return;
            }

            Zone northLawn = new Zone(
                    "North Lawn",
                    13.0,
                    54.0,
                    3.0
            );

            northLawn = zoneRepository.save(northLawn);

            TelemetryReading normalReading = new TelemetryReading(
                    northLawn,
                    LocalDateTime.of(2026, 8, 16, 21, 50),
                    13.4,
                    53.5,
                    ValveState.OPEN,
                    600,
                    22.0,
                    26.0,
                    null
            );

            TelemetryReading abnormalReading = new TelemetryReading(
                    northLawn,
                    LocalDateTime.of(2026, 8, 17, 18, 55),
                    17.0,
                    40.0,
                    ValveState.OPEN,
                    600,
                    22.0,
                    23.0,
                    null
            );

            telemetryRepository.save(normalReading);
            telemetryRepository.save(abnormalReading);

            logger.info("Seeded demo irrigation data.");
        };
    }
}