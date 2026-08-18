package com.damon.irrigationdiagnostics.diagnostic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistedDiagnosticFindingRepository
        extends JpaRepository<PersistedDiagnosticFinding, Long> {
}