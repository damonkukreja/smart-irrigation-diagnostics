import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarkdownComponent } from 'ngx-markdown';

import {
  IrrigationApiService,
  Zone,
  TelemetryReading,
  DiagnosticResponse
} from './irrigation-api.service';


@Component({
  selector: 'app-root',
  imports: [CommonModule, MarkdownComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  zones = signal<Zone[]>([]);
  telemetryReadings = signal<TelemetryReading[]>([]);
  selectedZoneId = signal<number | null>(null);

  diagnosticResult = signal<DiagnosticResponse | null>(null);
  diagnosticLoading = signal(false);

  zonesError = signal<string | null>(null);
  telemetryError = signal<string | null>(null);
  diagnosticError = signal<string | null>(null);



  constructor(private irrigationApi: IrrigationApiService) {
  }

  ngOnInit(): void {
    this.zonesError.set(null);

    this.irrigationApi
      .getZones()
      .subscribe({
        next: data => {
          console.log('Zones received:', data);
          this.zones.set(data);
        },
        error: error => {
          console.error('Failed to load zones:', error);
          this.zonesError.set('Unable to load irrigation zones.');
        }
      });
  }

  selectZone(zoneId: number): void {
    this.selectedZoneId.set(zoneId);
    this.telemetryReadings.set([]);
    this.telemetryError.set(null);
    this.diagnosticResult.set(null);

    this.irrigationApi
      .getTelemetryForZone(zoneId)
      .subscribe({
        next: data => {
          console.log('Telemetry received:', data);
          this.telemetryReadings.set(data);
        },
        error: error => {
          console.error('Failed to load telemetry:', error);
          this.telemetryError.set('Unable to load telemetry for this zone.');
        }
      });
  }

  runDiagnostics(telemetryReadingId: number): void {
    this.diagnosticLoading.set(true);
    this.diagnosticResult.set(null);
    this.diagnosticError.set(null);

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
          this.diagnosticError.set('Unable to run diagnostics for this reading.');
          this.diagnosticLoading.set(false);
        }
      });
  }
}

