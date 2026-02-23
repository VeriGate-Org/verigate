# OpenSanctions Adapter - CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with the OpenSanctions adapter.

## Build and Test Commands

### Building the Project
```bash
# Build the entire OpenSanctions adapter
mvn clean compile

# Build with all tests
mvn clean install

# Build specific module (from adapter root)
cd verigate-adapter-opensanctions-infrastructure && mvn clean compile
```

### Running Tests
```bash
# Run all tests in the adapter
mvn test

# Run specific test class
mvn test -Dtest=DefaultOpenSanctionsMatchingServiceTest

# Run tests with verbose output
mvn test -X
```

### Live Integration Testing
```bash
# OpenSanctions live API testing (requires credentials)
cd src/verigate-adapter-opensanctions-infrastructure
mvn test -Dtest=OpenSanctionsLiveIntegrationTest

# Quick connectivity test
java -cp target/test-classes:target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) verigate.adapter.opensanctions.infrastructure.integration.QuickLiveTest
```

### Code Quality
```bash
# Compile to check for compilation errors
mvn clean compile

# Check for dependency issues
mvn dependency:analyze
```

## High-Level Architecture

### OpenSanctions Adapter Overview
This is the **OpenSanctions API adapter** for the VeriGate verification platform. It provides sanctions screening capabilities by integrating with the OpenSanctions entity matching API.

### Clean Architecture Implementation
The adapter follows hexagonal (clean) architecture with three distinct layers:

```
verigate-adapter-opensanctions/
├── verigate-adapter-opensanctions-domain/           # Core business logic
├── verigate-adapter-opensanctions-application/      # Use cases & orchestration  
└── verigate-adapter-opensanctions-infrastructure/   # External integrations
```

### Key Architectural Components

#### 1. Domain Layer (`-domain/`)
- **Services**: `OpenSanctionsMatchingService` - Core service interface
- **Models**: Entity matching models (`EntityMatchRequest`, `EntityMatchResponse`, `ScoredEntity`, etc.)
- **Handlers**: `SanctionsScreeningCommandHandler` - Command processing interface
- **Mappers**: Business logic for mapping between VeriGate and OpenSanctions formats
- **Constants**: Domain-level constants and thresholds

#### 2. Application Layer (`-application/`)
- **Command Handlers**: `DefaultSanctionsScreeningCommandHandler` - Main entry point
- **Orchestration**: Coordinates entity matching, result analysis, and event publishing

#### 3. Infrastructure Layer (`-infrastructure/`)
- **HTTP Adapters**: Type-safe API clients (`OpenSanctionsHttpAdapter`, `OpenSanctionsApiAdapter`)
- **Configuration**: Environment-driven config (`OpenSanctionsApiConfiguration`, `ConfigurationValidator`)
- **DTOs**: Data transfer objects for API communication
- **Mappers**: DTO to domain model mapping
- **Services**: Default service implementations

### OpenSanctions API Integration Architecture

#### API Authentication
The adapter uses Bearer Token authentication:
- API key passed in `Authorization: Bearer <token>` header
- All HTTP requests include proper authentication

#### HTTP Client Architecture
```
OpenSanctionsHttpAdapter (base HTTP client)
└── OpenSanctionsApiAdapter (entity matching endpoints)
```

#### Service Implementation Pattern
- Interface in domain layer defines business operations
- Default implementation in infrastructure uses HTTP adapter
- Mock implementation in test layer for testing
- Dependency injection wires real vs. mock implementations

### Configuration Architecture

#### Environment-Driven Configuration
Required environment variables:
- `OPENSANCTIONS_API_KEY`

Optional environment variables:
- `OPENSANCTIONS_BASE_URL`, `OPENSANCTIONS_CONNECTION_TIMEOUT_MS`, etc.

#### Configuration Validation
- `ConfigurationValidator` validates required settings on startup
- `OpenSanctionsApiConfiguration` provides type-safe access to all settings
- Comprehensive defaults in `DomainConstants` and `application.properties`

### Key Integration Points

#### VeriGate Command Gateway Integration
- Implements `SanctionsScreeningCommandHandler` interface
- Receives `VerifyPartyCommand` from command gateway for `SANCTIONS_SCREENING` type
- Returns `VerificationResult` with standardized outcomes

#### Entity Matching Workflow
1. **Command Reception**: Gateway sends `VerifyPartyCommand` for sanctions screening
2. **Entity Mapping**: Convert party details to OpenSanctions entity format
3. **API Call**: Perform entity matching via OpenSanctions API
4. **Result Analysis**: Analyze match scores to determine verification outcome
5. **Response**: Return `VerificationResult` to gateway

### Verification Outcome Logic
Based on match scores from OpenSanctions:
- **High confidence match (≥0.9)**: `HARD_FAIL` - Likely sanctioned
- **Medium confidence match (≥0.7)**: `SOFT_FAIL` - Requires review
- **Low/no matches (<0.7)**: `SUCCEEDED` - Clean result

### Testing Strategy

#### Mock Architecture
- `MockOpenSanctionsMatchingService` for unit testing
- Mock responses include realistic scored entities and match data
- Test configurations with reduced timeouts and retry settings

#### Test Coverage
- Unit tests for service implementations and command handlers
- Integration tests (when API credentials available)
- Mock-based tests for all error scenarios

### Development Notes

#### Adding New API Endpoints
1. Add method to `OpenSanctionsMatchingService` interface
2. Implement in `DefaultOpenSanctionsMatchingService`
3. Add endpoint handling to `OpenSanctionsApiAdapter`
4. Create corresponding DTOs if needed
5. Add mock implementation for testing

#### Configuration Changes
1. Add constants to `EnvironmentConstants` and `DomainConstants`
2. Update `OpenSanctionsApiConfiguration` with getter methods
3. Add properties to `application.properties` files
4. Update validation in `ConfigurationValidator`
5. Document in `environment-variables.md`

#### Extending Entity Matching
The system currently supports Person entities. To add Company/Organization support:
1. Update `VerifyPartyCommandMapper` to detect entity type
2. Add company-specific property mapping
3. Update domain constants for company schemas
4. Add corresponding test cases

### OpenSanctions API Reference
- **Base URL**: https://api.opensanctions.org
- **Main Endpoint**: `/match/{dataset}` - Entity matching
- **Search Endpoint**: `/search/{dataset}` - Text-based search  
- **Health Check**: `/healthz` - Service health
- **Documentation**: https://www.opensanctions.org/docs/api/