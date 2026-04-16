# DeedsWeb Adapter - CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with the DeedsWeb adapter.

## Build and Test Commands

### Building the Project
```bash
# Build the entire DeedsWeb adapter
mvn clean compile

# Build with all tests
mvn clean install

# Build specific module (from adapter root)
cd verigate-adapter-deedsweb-infrastructure && mvn clean compile
```

### Running Tests
```bash
# Run all tests in the adapter
mvn test

# Run specific test class
mvn test -Dtest=DefaultDeedsWebMatchingServiceTest

# Run tests with verbose output
mvn test -X
```

### Code Quality
```bash
# Compile to check for compilation errors
mvn clean compile

# Check for dependency issues
mvn dependency:analyze
```

## High-Level Architecture

### DeedsWeb Adapter Overview
This is the **DeedsWeb adapter** for the VeriGate verification platform. It is responsible for
property-ownership verification by querying the South African DeedsWeb registry.

**Status:** the adapter currently ships REST-over-JSON scaffolding (cloned from an earlier
adapter). Live integration with the real DeedsWeb registry uses SOAP against
`http://deedssoap.deeds.gov.za:80/deeds-registration-soap/`. Replacing the REST scaffolding
with a real SOAP client (JAX-WS / Apache CXF, against the WSDL in
`docs/deeds-office/deeds-registration-soap.xml`) is a planned follow-up.

### Clean Architecture Implementation
The adapter follows hexagonal (clean) architecture with three distinct layers:

```
verigate-adapter-deedsweb/
├── verigate-adapter-deedsweb-domain/           # Core business logic
├── verigate-adapter-deedsweb-application/      # Use cases & orchestration
└── verigate-adapter-deedsweb-infrastructure/   # External integrations
```

### Key Architectural Components

#### 1. Domain Layer (`-domain/`)
- **Services**: `DeedsWebMatchingService` (entity-match scaffolding) and
  `PropertyOwnershipVerificationService` (live property-verification path)
- **Models**: Property models (`PropertyDetails`, `PropertyOwnershipCheck`,
  `OwnershipVerificationResult`) and entity-match models
  (`EntityMatchRequest`, `EntityMatchResponse`, `ScoredEntity`, etc.)
- **Handlers**: `PropertyVerificationCommandHandler` - command processing interface
- **Mappers**: `VerificationResultMapper`, `VerifyPartyCommandMapper`
- **Constants**: Domain-level constants and thresholds (`DomainConstants`)

#### 2. Application Layer (`-application/`)
- **Command Handlers**: `DefaultPropertyVerificationCommandHandler` - main entry point
- **Services**: `DefaultPropertyOwnershipVerificationService` - orchestrates property
  lookup and ownership matching; computes confidence in
  `calculateMatchConfidence(...)`

#### 3. Infrastructure Layer (`-infrastructure/`)
- **HTTP Adapters**: REST scaffolding (`DeedsWebHttpAdapter`, `DeedsWebApiAdapter`)
- **Configuration**: Environment-driven config (`DeedsWebApiConfiguration`,
  `ConfigurationValidator`)
- **DTOs**: Data transfer objects for API communication
- **Mappers**: DTO to domain model mapping (`DeedsWebDtoMapper`)
- **Services**: Default service implementations (`DefaultDeedsWebMatchingService`)

### DeedsWeb API Integration Architecture

#### API Authentication
The current REST scaffolding uses Bearer Token authentication:
- API key passed in `Authorization: Bearer <token>` header
- All HTTP requests include proper authentication

When the SOAP rewrite lands, authentication will follow whatever scheme the DeedsWeb
SOAP service requires (likely WS-Security username/token plus a whitelisted source IP).

#### HTTP Client Architecture
```
DeedsWebHttpAdapter (base HTTP client)
└── DeedsWebApiAdapter (entity matching endpoints)
```

#### Service Implementation Pattern
- Interface in domain layer defines business operations
- Default implementation in infrastructure uses HTTP adapter
- Mock implementation in test layer for testing
- Dependency injection wires real vs. mock implementations

### Configuration Architecture

#### Environment-Driven Configuration
Required environment variables:
- `DEEDSWEB_API_KEY`

Optional environment variables:
- `DEEDSWEB_BASE_URL`, `DEEDSWEB_CONNECTION_TIMEOUT_MS`,
  `DEEDSWEB_READ_TIMEOUT_MS`, `DEEDSWEB_RETRY_ATTEMPTS`,
  `DEEDSWEB_RETRY_DELAY_MS`

#### Configuration Validation
- `ConfigurationValidator` validates required settings on startup
- `DeedsWebApiConfiguration` provides type-safe access to all settings
- Comprehensive defaults in `DomainConstants` and `application.properties`

### Key Integration Points

#### VeriGate Command Gateway Integration
- Implements `PropertyVerificationCommandHandler` interface
- Receives `VerifyPartyCommand` from the command gateway for property-verification
  requests
- Returns `VerificationResult` with standardized outcomes

#### Property Verification Workflow
1. **Command Reception**: Gateway sends `VerifyPartyCommand` for property verification
2. **Property Lookup**: `DefaultPropertyOwnershipVerificationService.findPropertiesByOwner(...)`
   queries the DeedsWeb adapter for properties registered against the subject's ID number
3. **Ownership Check**: `checkOwnership(...)` walks the returned properties looking
   for an ID-number match
4. **Confidence Scoring**: `calculateMatchConfidence(...)` boosts confidence based on
   how closely the queried name matches the registered owner name
5. **Response**: Return `VerificationResult` to gateway

### Verification Outcome Logic
Property-verification outcomes are derived from
`DefaultPropertyOwnershipVerificationService.calculateMatchConfidence(...)`. The
confidence score combines an exact ID-number match (base `0.7`) with name-similarity
boosts up to `1.0`. The match-score thresholds in `DomainConstants` (`HIGH_/MEDIUM_/
LOW_MATCH_THRESHOLD`) remain available for the entity-match scaffolding path.

### Testing Strategy

#### Mock Architecture
- `MockDeedsWebMatchingService` for unit testing
- Mock responses include realistic property records
- Test configurations with reduced timeouts and retry settings

#### Test Coverage
- Unit tests for service implementations and command handlers
- Mock-based tests for all error scenarios

### Development Notes

#### Adding New API Endpoints
1. Add method to `DeedsWebMatchingService` interface
2. Implement in `DefaultDeedsWebMatchingService`
3. Add endpoint handling to `DeedsWebApiAdapter`
4. Create corresponding DTOs if needed
5. Add mock implementation for testing

#### Configuration Changes
1. Add constants to `EnvironmentConstants` and `DomainConstants`
2. Update `DeedsWebApiConfiguration` with getter methods
3. Add properties to `application.properties` files
4. Update validation in `ConfigurationValidator`
5. Document in `environment-variables.md`

### DeedsWeb API Reference
- **SOAP endpoint**: `http://deedssoap.deeds.gov.za:80/deeds-registration-soap/`
- **WSDL**: see `docs/deeds-office/deeds-registration-soap.xml`
- **Network**: requires source IP whitelisting (NAT EIP `13.246.247.144`)
- **Note**: The REST scaffolding in this module does not yet talk to the real
  DeedsWeb service — the SOAP client is a planned follow-up.
