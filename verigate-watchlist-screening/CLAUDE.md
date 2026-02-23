# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

The **Watchlist Screening Microservice** is a Clean Architecture-based domain service that centralizes all watchlist screening business logic. It follows Domain-Driven Design (DDD) principles and implements an event-driven architecture for processing screening requests against various watchlist providers (OpenSanctions, WorldCheck, etc.).

## Build and Test Commands

### Building the Service
```bash
# Build the entire watchlist screening service
mvn clean compile -pl src/verigate-watchlist-screening

# Build with all tests
mvn clean install -pl src/verigate-watchlist-screening

# Build specific module
mvn clean compile -pl src/verigate-watchlist-screening/verigate-watchlist-screening-domain
```

### Running Tests
```bash
# Run all tests across all modules
mvn test -pl src/verigate-watchlist-screening/verigate-watchlist-screening-domain,src/verigate-watchlist-screening/verigate-watchlist-screening-application,src/verigate-watchlist-screening/verigate-watchlist-screening-infrastructure

# Run tests for specific module
mvn test -pl src/verigate-watchlist-screening/verigate-watchlist-screening-domain

# Run specific test class
mvn test -pl src/verigate-watchlist-screening/verigate-watchlist-screening-domain -Dtest=DefaultCreateWatchlistScreeningFactoryTest

# Run tests with verbose output
mvn test -pl src/verigate-watchlist-screening/verigate-watchlist-screening-domain -X
```

## Architecture

### Clean Architecture Implementation
```
verigate-watchlist-screening/
├── verigate-watchlist-screening-domain/           # Core business logic (no external dependencies)
│   ├── commands/                                  # Command objects
│   ├── events/                                    # Domain events
│   ├── factories/                                 # Aggregate creation
│   ├── handlers/                                  # Command handler interfaces
│   ├── models/                                    # Aggregates, entities, value objects
│   ├── repositories/                              # Repository interfaces
│   └── services/                                  # Domain services
├── verigate-watchlist-screening-application/      # Use cases & orchestration
│   └── handlers/                                  # Command handler implementations
└── verigate-watchlist-screening-infrastructure/   # External integrations
    └── repositories/                              # Repository implementations
```

### Dependency Direction
**Infrastructure** → **Application** → **Domain**
- Domain layer has zero external dependencies
- Application layer depends only on domain interfaces
- Infrastructure layer provides concrete implementations

## Domain Model

### Core Aggregate: WatchlistScreeningAggregateRoot
The main aggregate that orchestrates the screening lifecycle:
- Manages screening status (`PENDING`, `IN_PROGRESS`, `COMPLETED`, `FAILED`)
- Processes `ProviderResult` objects from multiple screening providers
- Applies decision matrix logic based on confidence scores
- Generates domain events based on screening outcomes

### Key Domain Models

#### ScreeningRequest (Record)
- Supports both `PERSON` and `COMPANY` entity types
- Contains identifying information (firstName, lastName, dateOfBirth, countryOfResidence)
- Factory methods: `ScreeningRequest.person()`, `ScreeningRequest.company()`

#### ProviderResult (Record)
- Raw results from external screening providers
- Contains provider name, list of `MatchedEntity` objects, and result status
- Factory methods: `ProviderResult.success()`, `ProviderResult.failure()`

#### MatchedEntity (Record)
- Standardized representation of watchlist matches across providers
- Contains entity ID, name, confidence score, categories (PEP, Sanctions), and datasets
- Helper methods for risk assessment and categorization

#### ScreeningDecision (Record)
- Final decision with status: `CLEARED`, `MATCH_CONFIRMED`, `REVIEW_REQUIRED`, `SOFT_FAILED`, `SYSTEM_OUTAGE`
- Contains confidence score, reasoning, and matched entity details

### Decision Matrix Logic
```
Confidence Score    Action              Event Generated
>= 90%             Auto-reject         WatchlistMatchConfirmedEvent
70-89%             Manual review       WatchlistMatchReviewRequiredEvent  
50-69%             Partner policy      Context-dependent event
< 50%              Auto-clear          WatchlistScreeningClearedEvent
Error/Timeout      System handling     WatchlistScreeningHardFailedEvent or WatchlistScreeningSoftFailedEvent
```

## Domain Events

### Event Hierarchy
All events extend `WatchlistScreeningEvent` base class:

- **WatchlistScreeningClearedEvent**: No significant matches found
- **WatchlistMatchConfirmedEvent**: High-confidence match requiring action
- **WatchlistMatchReviewRequiredEvent**: Medium-confidence match needs human review
- **WatchlistScreeningSoftFailedEvent**: Transient failure, retry possible
- **WatchlistScreeningHardFailedEvent**: Permanent failure, manual intervention needed
- **WatchlistScreeningRawResultReceivedEvent**: Input event triggering evaluation

## CQRS Pattern Implementation

### Commands
- **CreateWatchlistScreeningCommand**: Initiates new screening process
  - Contains partnerId and ScreeningRequest
  - Processed by `CreateWatchlistScreeningCommandHandler`

### Command Processing Flow
1. **Command Reception**: `DefaultCreateWatchlistScreeningCommandHandler.handle()`
2. **Aggregate Creation**: `CreateWatchlistScreeningFactory.create()`
3. **Persistence**: `WatchlistScreeningRepository.addOrUpdate()`
4. **Event Generation**: Domain events emitted based on screening results

## Key Interfaces and Patterns

### Factory Pattern
- **CreateWatchlistScreeningFactory**: Interface for creating screening aggregates
  - `create(CreateWatchlistScreeningCommand)` - Create from command
  - `createPersonScreeningRequest()` - Create person-specific requests
  - `createCompanyScreeningRequest()` - Create company-specific requests
  - `createWatchlistScreening()` - Create aggregate with specific parameters

### Repository Pattern
- **WatchlistScreeningRepository**: Domain interface for persistence
  - `get(UUID)` - Retrieve by screening ID
  - `addOrUpdate(WatchlistScreeningAggregateRoot)` - Persist changes
- **InMemoryWatchlistScreeningRepository**: Development/testing implementation

### Service Layer
- **WatchlistScreeningService**: Core domain service orchestrating business logic
  - `createScreeningJob()` - Initiate new screening
  - `processProviderResult()` - Handle provider responses
  - `reEvaluateScreening()` - Re-run decision logic
- **DecisionMatrixService**: Encapsulates threshold evaluation logic
- **EventFactoryService**: Creates appropriate domain events

## Testing Strategy

### Test Organization
```bash
# Domain Tests (28 tests)
domain/models/WatchlistScreeningAggregateRootTest.java          # 7 tests
domain/factories/DefaultCreateWatchlistScreeningFactoryTest.java # 5 tests  
domain/commands/CreateWatchlistScreeningTest.java              # 2 tests
domain/services/WatchlistScreeningServiceImplTest.java         # 4 tests
domain/services/DecisionMatrixServiceTest.java                 # 10 tests

# Application Tests (4 tests)
application/handlers/DefaultCreateWatchlistScreeningCommandHandlerTest.java

# Infrastructure Tests (4 tests)  
infrastructure/repositories/InMemoryWatchlistScreeningRepositoryTest.java
```

### Testing Patterns Used
- **Mockito** for external dependency mocking
- **Factory methods** for consistent test data creation
- **Behavior verification** testing business rule compliance
- **State-based testing** for aggregate state transitions

### Key Test Scenarios
- Decision matrix thresholds and confidence scoring
- Multi-provider result aggregation and deduplication
- Error handling and retry logic
- Command processing workflows
- Repository persistence operations

## Configuration Architecture

### Partner-Specific Configuration
The domain model supports partner-specific configuration via `ScreeningConfiguration`:
- Configurable confidence thresholds for different decision outcomes
- Provider selection and weighting
- PEP and sanctions-specific escalation rules
- Custom watchlist filtering

### Extensibility Points
- **New Providers**: Implement provider-specific result mapping to `MatchedEntity`
- **Decision Rules**: Extend `DecisionMatrixService` with new evaluation logic
- **Event Types**: Add new domain events for specific business scenarios
- **Entity Types**: Extend beyond PERSON/COMPANY if needed

## Development Guidelines

### Adding New Decision Logic
1. Update `DecisionMatrixService` with new evaluation methods
2. Add corresponding test scenarios in `DecisionMatrixServiceTest`
3. Update domain events if new outcomes are possible
4. Ensure configuration supports new thresholds if needed

### Extending Provider Support
1. Map provider-specific responses to standardized `MatchedEntity` format
2. Handle provider-specific error scenarios in service layer
3. Add provider-specific test scenarios
4. Update configuration model if provider needs custom settings

### Domain Event Handling
1. All state changes should generate appropriate domain events
2. Events should contain sufficient context for downstream processing
3. Use `EventFactoryService` for consistent event creation
4. Test event generation in aggregate tests