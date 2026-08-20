import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface Zone {
  id: number;
  name: string;
  expectedFlowLpm: number;
  expectedPressurePsi: number;
  minimumMoistureIncreasePct: number;
}

interface TelemetryReading {
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
interface DiagnosticFinding {
  id: number;
  anomalyType: string;
  metric: string;
  observedValue: number;
  expectedValue: number;
  deviationPct: number;
}

interface DiagnosticResponse {
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
@Component({
  selector: 'app-root',
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  zones = signal<Zone[]>([]);
  telemetryReadings = signal<TelemetryReading[]>([]);
  selectedZoneId = signal<number | null>(null);

  diagnosticResult = signal<DiagnosticResponse | null>(null);
  diagnosticLoading = signal(false);

  runDiagnostics(telemetryReadingId: number): void {
    this.diagnosticLoading.set(true);
    this.diagnosticResult.set(null);

    this.http
      .post<DiagnosticResponse>(
        `http://localhost:8080/api/diagnostics/telemetry/${telemetryReadingId}`,
        {}
      )
      .subscribe({
        next: data => {
          console.log('Diagnostic result:', data);
          this.diagnosticResult.set(data);
          this.diagnosticLoading.set(false);
        },
        error: error => {
          console.error('Diagnostic request failed:', error);
          this.diagnosticLoading.set(false);
        }
      });
  }

  constructor(private http: HttpClient) {
  }

  ngOnInit(): void {
    this.http
      .get<Zone[]>('http://localhost:8080/api/zones')
      .subscribe(data => {
        console.log('Zones received:', data);
        this.zones.set(data);
      });
  }

  selectZone(zoneId: number): void {
    this.selectedZoneId.set(zoneId);

    this.http
      .get<TelemetryReading[]>(
        `http://localhost:8080/api/telemetry/zone/${zoneId}`
      )
      .subscribe(data => {
        console.log('Telemetry received:', data);
        this.telemetryReadings.set(data);
      });
  }
}
