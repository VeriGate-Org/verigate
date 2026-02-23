# VeriGate Watchlist Screening Reactor

This module serves as a reactor for building and integrating all VeriGate Watchlist Screening modules. It follows the same architectural patterns as the core modules but acts as a coordinator and bootstrapper for the entire system.

## Module Structure

The reactor follows the existing VeriGate Watchlist Screening structure:

```
verigate-watchlist-screening-reactor/
├── src/
│   └── main/
│       └── java/
│           └── verigate/
│               └── watchlist/
│                   └── reactor/
│                       ├── application/  - Reactor application services
│                       └── config/       - Guice configuration for DI
└── pom.xml             - Maven build configuration
```

## Architecture Overview

The project is organized into the following layers, from innermost to outermost:

### 1. Domain Layer

The core business rules and entities reside here. This layer:
- Contains the business entities, value objects, and aggregates
- Defines domain services, repositories interfaces, and domain events
- Has no dependencies on external frameworks or other layers
- Implements use case-specific business rules

Key Components:
- `WatchlistScreeningAggregateRoot`: The core entity representing a watchlist screening process
- `ScreeningRequest`: Value object for representing screening request details
- `CreateWatchlistScreeningCommandHandler`: Handles commands for creating watchlist screenings
- `WatchlistScreeningRepository`: Interface for persistence operations

### 2. Application Layer

This layer coordinates the use cases of the application by:
- Orchestrating the flow of data to and from domain entities
- Implementing application-specific business rules
- Defining the command handlers and service interfaces
- Remaining free of external framework dependencies

Key Components:
- `WatchlistScreeningApplicationService`: Main application service coordinating use cases
- `ApplicationEventPublisher`: Handles application-level events

### 3. Infrastructure Layer

This layer provides concrete implementations of the interfaces defined in the inner layers:
- Repository implementations
- External service integrations
- Database access
- Framework-specific code

Key Components:
- `InMemoryWatchlistScreeningRepository`: In-memory implementation of repository
- `WatchlistScreeningModule`: Guice module for dependency injection

### 4. Presentation Layer

This layer handles the delivery mechanisms (APIs, UIs) and:
- Transforms data for presentation
- Handles HTTP requests/responses
- Manages UI components
- Contains controllers/presenters

## Dependency Rule

The fundamental rule of Clean Architecture is that dependencies point inward. This means:
- Domain layer has no dependencies on other layers
- Application layer depends only on Domain
- Infrastructure layer depends on Domain and Application
- Presentation layer depends on Domain and Application

## Using Google Guice for DI

We use Google Guice for dependency injection to:
1. Decouple object creation from object usage
2. Enable testability by easily swapping implementations
3. Maintain the dependency rule through runtime binding of interfaces to implementations

Example:

```java
// Define bindings in a module
public class WatchlistScreeningModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WatchlistScreeningRepository.class).to(InMemoryWatchlistScreeningRepository.class);
        // Other bindings...
    }
}

// Inject dependencies
@Inject
public DefaultWatchlistScreeningService(WatchlistScreeningRepository repository) {
    this.repository = repository;
}
```

## Testing Strategy

The architecture facilitates testing at all levels:

1. **Domain Tests**: Pure unit tests without mocks
2. **Application Tests**: Tests with mocks for repositories and services
3. **Integration Tests**: Tests that verify infrastructure implementations
4. **End-to-End Tests**: Tests that exercise the entire system

## Building the Project

This module serves as a reactor that builds all modules in the correct order:

```bash
mvn clean install
```

## Contributing

When contributing to this project, please follow these principles:
1. Respect layer boundaries and dependencies
2. Write tests for all new code
3. Use interfaces to define dependencies
4. Keep the domain model clean and framework-independent
