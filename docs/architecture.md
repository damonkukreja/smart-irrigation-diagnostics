# Architecture

## 1. Purpose

The Smart Irrigation Diagnostics Platform is a modular full-stack application that evaluates irrigation telemetry using deterministic Java rules and then optionally uses a local large language model to explain the verified findings.

The core architectural principle is:

> Deterministic software decides what happened. AI only helps explain it.

This separation keeps anomaly classification reproducible, testable, and independent of model availability.

---

## 2. High-Level System Architecture

    Angular Frontend
          |
          | REST over HTTP
          v
    Spring Boot Backend
          |
          +-----------------------------+
          |                             |
          v                             v
    Deterministic                 PostgreSQL
    Analysis Engine               Persistence
          |
          v
    Verified Findings
          |
          v
    InferenceProvider
          |
          v
    Ollama
          |
          v
    Qwen 3.5 9B

The backend is implemented as a modular monolith rather than as separate microservices.

This keeps deployment and development simple while still preserving separation between major responsibilities.

---

## 3. Frontend Architecture

The Angular frontend currently follows this structure:

    App Component
          |
          v
    IrrigationApiService
          |
          v
    Spring Boot REST API

### App Component

The `App` component is responsible for:

- rendering zones
- rendering telemetry
- managing selected zone state
- managing diagnostic loading state
- displaying user-facing errors
- displaying diagnostic results
- rendering AI explanations
- displaying inference metrics

Angular Signals are used for reactive state.

Examples include:

    zones
    telemetryReadings
    selectedZoneId
    diagnosticResult
    diagnosticLoading
    zonesError
    telemetryError
    diagnosticError

### IrrigationApiService

The `IrrigationApiService` owns HTTP communication with the backend.

It exposes methods such as:

    getZones()
    getTelemetryForZone(zoneId)
    runDiagnostics(telemetryReadingId)

This keeps backend URLs and HTTP concerns out of the main UI component.

---

## 4. Backend Package Structure

    com.damon.irrigationdiagnostics
    |
    +-- analysis
    |   +-- AnomalyRule
    |   +-- AnomalyType
    |   +-- DiagnosticFinding
    |   +-- TelemetryAnalyzer
    |   +-- HighFlowRule
    |   +-- LowFlowRule
    |   +-- PressureDropRule
    |   +-- LowMoistureResponseRule
    |   +-- UnexpectedFlowRule
    |
    +-- config
    |   +-- CorsConfig
    |   +-- DemoDataInitializer
    |
    +-- diagnostic
    |   +-- DiagnosticController
    |   +-- DiagnosticService
    |   +-- DiagnosticRun
    |   +-- DiagnosticStatus
    |   +-- DiagnosticResponse
    |   +-- PersistedDiagnosticFinding
    |   +-- DiagnosticRunRepository
    |   +-- PersistedDiagnosticFindingRepository
    |
    +-- inference
    |   +-- InferenceProvider
    |   +-- OllamaInferenceProvider
    |   +-- InferenceResult
    |   +-- InferenceRun
    |   +-- InferenceRunRepository
    |
    +-- telemetry
    |   +-- TelemetryController
    |   +-- TelemetryService
    |   +-- TelemetryReading
    |   +-- TelemetryRepository
    |   +-- CreateTelemetryRequest
    |   +-- TelemetryNotFoundException
    |   +-- ValveState
    |
    +-- zone
        +-- ZoneController
        +-- ZoneService
        +-- Zone
        +-- ZoneRepository
        +-- CreateZoneRequest
        +-- ZoneNotFoundException

---

## 5. Backend Layering

The backend follows a standard layered request flow:

    HTTP Request
         |
         v
    Controller
         |
         v
    Service
         |
         v
    Repository
         |
         v
    PostgreSQL

### Controllers

Controllers define HTTP boundaries.

Examples:

- `ZoneController`
- `TelemetryController`
- `DiagnosticController`

They receive requests, delegate to services, and return responses.

### Services

Services contain application and business logic.

Examples:

- `ZoneService`
- `TelemetryService`
- `DiagnosticService`
- `TelemetryAnalyzer`

### Repositories

Repositories provide database access using Spring Data JPA.

Examples:

- `ZoneRepository`
- `TelemetryRepository`
- `DiagnosticRunRepository`
- `PersistedDiagnosticFindingRepository`
- `InferenceRunRepository`

---

## 6. Core Domain Entities

### Zone

Represents an irrigation zone and its expected operating values.

Important fields:

- id
- name
- expected flow
- expected pressure
- minimum expected moisture increase

### TelemetryReading

Represents one sensor snapshot or irrigation event.

Important fields:

- id
- zone
- recorded time
- flow
- pressure
- valve state
- runtime
- initial soil moisture
- final soil moisture
- optional error code

Relationship:

    Zone 1 ---- * TelemetryReading

Many telemetry readings may belong to one zone.

### DiagnosticRun

Represents one execution of the diagnostic pipeline.

Important fields:

- id
- telemetry reading
- status
- created timestamp

Possible statuses include:

- `COMPLETED`
- `COMPLETED_WITHOUT_AI`
- `FAILED`

### PersistedDiagnosticFinding

Represents a deterministic anomaly finding saved to the database.

Important fields:

- anomaly type
- metric
- observed value
- expected value
- deviation percentage

Relationship:

    DiagnosticRun 1 ---- * PersistedDiagnosticFinding

### InferenceRun

Represents one local AI inference attempt.

Important fields:

- model
- prompt tokens
- output tokens
- latency
- generation speed
- success flag
- error message
- created timestamp

Relationship:

    DiagnosticRun 1 ---- 0..1 InferenceRun

for the current workflow.

---

## 7. Deterministic Analysis Engine

The deterministic engine is intentionally independent from the local LLM.

The flow is:

    TelemetryReading
          |
          v
    TelemetryAnalyzer
          |
          v
    List<AnomalyRule>
          |
          v
    DiagnosticFinding[]

Each anomaly rule implements the same interface:

    AnomalyRule

Conceptually:

    evaluate(TelemetryReading)
        -> Optional<DiagnosticFinding>

The analyzer does not need to know the internal logic of each rule.

It simply iterates over all registered rules and collects findings.

This supports extensibility because a new rule can be added without rewriting the analyzer.

---

## 8. Deterministic Rules

### HIGH_FLOW

    valve = OPEN
    AND
    flow > expectedFlow × 1.20

### LOW_FLOW

    valve = OPEN
    AND
    flow < expectedFlow × 0.70

### PRESSURE_DROP

    valve = OPEN
    AND
    pressure < expectedPressure × 0.80

### LOW_MOISTURE_RESPONSE

    valve = OPEN
    AND
    runtime >= 300 seconds
    AND
    finalMoisture - initialMoisture < minimumExpectedMoistureIncrease

### UNEXPECTED_FLOW

    valve = CLOSED
    AND
    flow > 1.0 L/min

These rules are explicit and reproducible.

---

## 9. Diagnostic Request Lifecycle

When the frontend sends:

    POST /api/diagnostics/telemetry/{telemetryReadingId}

the backend follows this sequence:

    DiagnosticController
          |
          v
    DiagnosticService
          |
          v
    Load TelemetryReading
          |
          v
    TelemetryAnalyzer
          |
          v
    Deterministic Findings
          |
          +-----------------------------+
          |                             |
          v                             v
    Build guarded AI prompt       Save DiagnosticRun
          |                             |
          v                             v
    InferenceProvider            Save Findings
          |
          v
    OllamaInferenceProvider
          |
          v
    Local Ollama API
          |
          v
    InferenceResult
          |
          v
    Save InferenceRun
          |
          v
    DiagnosticResponse

The frontend receives both deterministic findings and the optional AI explanation in one response.

---

## 10. AI Boundary

The AI layer is not the source of truth.

The prompt contains only verified deterministic findings.

The model is instructed not to:

- add anomaly classifications
- remove anomaly classifications
- rename anomaly classifications
- invent measurements
- invent device states
- invent error codes
- claim a confirmed root cause
- use strong causal language connecting hypotheses to findings

The model is allowed to:

- summarize verified findings
- restate measurements
- suggest neutral verification actions
- provide investigation steps

This design minimizes the risk of treating probabilistic model output as deterministic system state.

---

## 11. Inference Abstraction

`DiagnosticService` depends on:

    InferenceProvider

rather than directly depending on Ollama.

The concrete implementation is:

    OllamaInferenceProvider

This provides several benefits:

- easier unit testing
- easier failure simulation
- provider replaceability
- reduced coupling
- clearer separation of concerns

A different local model provider could be added later without changing the deterministic analysis engine.

---

## 12. Local Ollama Integration

The current Ollama implementation sends HTTP requests to:

    http://localhost:11434/api/generate

Current model:

    qwen3.5:9b

The request uses:

    stream = false
    think = false

The provider converts the Ollama response into an `InferenceResult`.

The result includes:

- explanation text
- prompt tokens
- output tokens
- total latency
- generation tokens per second

---

## 13. Graceful AI Failure Path

One of the most important failure paths is local AI unavailability.

If Ollama is stopped or unreachable:

    TelemetryReading
          |
          v
    Deterministic Analysis
          |
          v
    Findings Produced
          |
          v
    Ollama Call Fails
          |
          v
    Exception Caught
          |
          v
    Diagnostic Status =
    COMPLETED_WITHOUT_AI
          |
          v
    Deterministic Findings Still Returned

The fallback explanation indicates that AI is unavailable.

The deterministic findings remain valid because they were computed before AI inference.

This behavior is covered by `DiagnosticServiceTest`.

---

## 14. Frontend Failure Handling

The Angular frontend maintains explicit error state.

Examples:

- `zonesError`
- `telemetryError`
- `diagnosticError`

This allows the UI to display messages such as:

    Unable to load irrigation zones.

instead of requiring the user to inspect the browser console.

The diagnostics UI also displays a loading state while local inference is running.

---

## 15. CORS Configuration

During local development:

- Angular runs on `http://localhost:4200`
- Spring Boot runs on `http://localhost:8080`

`CorsConfig` allows requests from the Angular development origin to backend routes under:

    /api/**

This resolves browser same-origin restrictions while keeping the allowed development origin explicit.

---

## 16. Reproducible Demo Data

`DemoDataInitializer` runs during Spring Boot startup.

It checks:

    zoneRepository.count()

If zones already exist:

    Demo data already present; skipping initialization.

If the database is empty, it creates:

- North Lawn
- one normal telemetry reading
- one abnormal telemetry reading

This prevents duplicate demo records while making a fresh environment easy to populate.

---

## 17. Demo Data

### Zone

    North Lawn
    Expected Flow: 13 L/min
    Expected Pressure: 54 PSI
    Minimum Moisture Increase: 3%

### Normal Reading

    Flow: 13.4 L/min
    Pressure: 53.5 PSI
    Valve: OPEN
    Runtime: 600 seconds
    Moisture: 22% -> 26%

Expected:

    no anomaly findings

### Abnormal Reading

    Flow: 17 L/min
    Pressure: 40 PSI
    Valve: OPEN
    Runtime: 600 seconds
    Moisture: 22% -> 23%

Expected:

    HIGH_FLOW
    LOW_MOISTURE_RESPONSE
    PRESSURE_DROP

---

## 18. Testing Strategy

The backend currently contains targeted tests for important behaviors.

### TelemetryAnalyzerTest

Covers:

- normal telemetry produces no findings
- abnormal telemetry produces expected anomalies
- closed valve with flow produces `UNEXPECTED_FLOW`

### DiagnosticControllerTest

Covers:

- missing telemetry returns HTTP 404

### DiagnosticServiceTest

Covers:

- AI failure does not remove deterministic findings
- status becomes `COMPLETED_WITHOUT_AI`
- fallback explanation is returned
- zero inference metrics are returned when inference fails

The testing strategy focuses on deterministic business logic and failure behavior.

---

## 19. Important Design Tradeoffs

### Modular Monolith vs. Microservices

A modular monolith was selected because the project does not currently require independent deployment or scaling of components.

Advantages:

- simpler local setup
- easier debugging
- fewer infrastructure dependencies
- still allows package-level separation

### JPA `ddl-auto=update`

Hibernate schema update is currently used for development convenience.

Tradeoff:

- fast for development
- not ideal for production schema management

A production-oriented improvement would be Flyway or Liquibase migrations.

### Direct Entity Exposure

Some API responses currently expose JPA-backed data directly.

Tradeoff:

- reduces boilerplate for the MVP
- creates tighter coupling between persistence and API shape

A larger production system would likely use dedicated response DTOs.

### Local Model vs. Cloud API

Ollama was selected because the project is intended to demonstrate local inference.

Advantages:

- no external API dependency
- no per-request API cost
- privacy
- direct access to inference metrics
- graceful offline behavior

Tradeoff:

- local hardware requirements
- model startup and runtime latency
- setup complexity

---

## 20. Future Architecture Improvements

Potential improvements include:

- Flyway migrations
- structured AI response DTOs
- configurable Ollama model and base URL
- Docker Compose
- historical telemetry visualization
- documentation retrieval
- authentication and authorization
- automated end-to-end tests
- richer telemetry history
- diagnostic severity levels
- frontend component decomposition
- request retry and timeout policies
- production logging and metrics



