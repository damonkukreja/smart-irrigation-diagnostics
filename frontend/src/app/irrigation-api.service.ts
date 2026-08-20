import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Zone {
  id: number;
  name: string;
  expectedFlowLpm: number;
  expectedPressurePsi: number;
  minimumMoistureIncreasePct: number;
}

export interface TelemetryReading {
  id: number;
  recordedAt: string;
  flowLpm: number;
  pressurePsi: number;
  valveState: string;
  runtimeSeconds: number;
  initialSoilMoisturePct: number;
  finalSoilMoisturePct: number;
  errorCode: string | null;
}

export interface DiagnosticFinding {
  id: number;
  anomalyType: string;
  metric: string;
  observedValue: number;
  expectedValue: number;
  deviationPct: number;
}

export interface DiagnosticResponse {
  diagnosticRunId: number;
  status: string;
  createdAt: string;
  findings: DiagnosticFinding[];
  aiExplanation: string;
  promptTokens: number;
  outputTokens: number;
  totalLatencyMs: number;
  generationTokensPerSecond: number;
}

@Injectable({
  providedIn: 'root'
})
export class IrrigationApiService {

  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {
  }

  getZones(): Observable<Zone[]> {
    return this.http.get<Zone[]>(`${this.baseUrl}/zones`);
  }

  getTelemetryForZone(zoneId: number): Observable<TelemetryReading[]> {
    return this.http.get<TelemetryReading[]>(
      `${this.baseUrl}/telemetry/zone/${zoneId}`
    );
  }

  runDiagnostics(telemetryReadingId: number): Observable<DiagnosticResponse> {
    return this.http.post<DiagnosticResponse>(
      `${this.baseUrl}/diagnostics/telemetry/${telemetryReadingId}`,
      {}
    );
  }
}
