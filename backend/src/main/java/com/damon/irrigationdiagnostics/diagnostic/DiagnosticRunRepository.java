package com.damon.irrigationdiagnostics.diagnostic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticRunRepository extends JpaRepository<DiagnosticRun, Long> {
}