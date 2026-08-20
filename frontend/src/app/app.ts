import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  IrrigationApiService,
  Zone,
  TelemetryReading,
  DiagnosticResponse
} from './irrigation-api.service';


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



  constructor(private irrigationApi: IrrigationApiService) {
  }

  ngOnInit(): void {
    this.irrigationApi
      .getZones()
      .subscribe(data => {
        console.log('Zones received:', data);
        this.zones.set(data);
      });
  }

  selectZone(zoneId: number): void {
    this.selectedZoneId.set(zoneId);

    this.irrigationApi
      .getTelemetryForZone(zoneId)
      .subscribe(data => {
        console.log('Telemetry received:', data);
        this.telemetryReadings.set(data);
      });
  }

  runDiagnostics(telemetryReadingId: number): void {
    this.diagnosticLoading.set(true);
    this.diagnosticResult.set(null);

    this.irrigationApi
      .runDiagnostics(telemetryReadingId)
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
  }}

