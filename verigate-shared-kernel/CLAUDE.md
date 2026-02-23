# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

VeriGate Shared Kernel is a Java 21 Maven library that provides shared domain-driven design (DDD) patterns, infrastructure components, and utilities for the VeriGate microservices ecosystem. It implements clean architecture principles with clear separation between domain, application, and infrastructure layers.

## Build and Development Commands

### Build and Test
```bash
mvn clean install          # Build and run all tests
mvn test                  # Run unit tests only
mvn test -Dtest=*IT       # Run integration tests only
```

### Version Management
```bash
mvn versions:set -DremoveSnapshot                    # Remove SNAPSHOT for release
mvn versions:set -DnewVersion=X.Y.Z-SNAPSHOT        # Set next development version
```

### Publishing
```bash
mvn deploy                # Deploy to GitHub Packages (requires authentication)
```

## Architecture Overview

### Domain Layer (`src/main/java/domain/`)
- **AggregateRoot**: Base class for domain aggregates with command/event idempotency, optimistic locking, and memento pattern support
- **Commands**: CQRS command pattern with `CommandDispatcher` and `CommandHandler` interfaces
- **Events**: Event-driven architecture with `EventPublisher` and domain events
- **Invariants**: Business rule validation with `Specification` pattern
- **Value Objects**: Immutable domain objects (DateRange, MobileNumber, Percentage, etc.)

### Application Layer (`src/main/java/application/`)
- **EventHandler**: Application-level event handlers for cross-aggregate coordination
- **InMemoryEventStoreHandler**: Simple event store implementation for testing

### Infrastructure Layer (`src/main/java/infrastructure/`)
- **AWS Integration**: SQS, DynamoDB, S3, Kinesis, Secrets Manager, Lambda handlers
- **Resilience**: Circuit breakers, retry mechanisms, backoff strategies
- **Serialization**: JSON/Avro serialization with Jackson
- **Messaging**: Message queues, dead letter queues, invalid message handling

### Cross-cutting Concerns (`src/main/java/crosscutting/`)
- **Validation**: BFF validators with custom annotations
- **Metrics**: Datadog integration and method interceptors
- **Patterns**: Factory, Memento, Builder patterns
- **Utilities**: UUID generation, parameter validation, template parsing

## Key Design Patterns

### Command Pattern
- Commands implement `BaseCommand` with unique IDs for idempotency
- `CommandDispatcher` routes commands to appropriate handlers
- Support for HTTP, SQS, and scheduled command dispatching

### Event Sourcing
- Domain events extend `BaseEvent` with logical clock readings
- `EventPublisher` implementations for Kinesis and in-process handling
- Event idempotency using logical clocks and natural keys

### Resilience Patterns
- Circuit breakers with configurable failure thresholds
- Retry decorators with exponential backoff and jitter
- Dead letter queue handling for failed messages

## Testing Approach

### Unit Tests
- Located in `src/test/java/` mirroring main source structure
- Uses JUnit 5, Mockito, and custom test models
- Focus on domain logic and business rules

### Integration Tests
- Classes ending with `IT` (e.g., `DynamoDBReorderMessageQueueTestIT`)
- Uses Testcontainers for AWS LocalStack testing
- Tests infrastructure components with real AWS services

### Test Dependencies
- JUnit 5 for test framework
- Mockito for mocking
- Testcontainers for integration testing
- REST Assured for API testing

## AWS Integration

### Services Used
- **DynamoDB**: Enhanced client for aggregate storage
- **SQS**: Message queuing with dead letter queue support
- **S3**: File storage with presigned URLs
- **Kinesis**: Event streaming
- **Secrets Manager**: Configuration and credential management
- **Lambda**: Serverless function handlers

### Lambda Handlers
- `ResilientSqsCommandLambdaHandler`: SQS-triggered command processing
- `ResilientSqsEventLambdaHandler`: SQS-triggered event handling
- `ResilientKinesisHandler`: Kinesis stream processing
- Error handling with retry and dead letter queue logic

## Code Conventions

### Package Structure
- `domain.*`: Core business logic, no infrastructure dependencies
- `application.*`: Application services coordinating domain objects
- `infrastructure.*`: External system integrations
- `crosscutting.*`: Shared utilities and patterns

### Error Handling
- `PermanentException`: Business rule violations, no retry
- `TransientException`: Infrastructure failures, retry possible
- `DeserializeException`: Message parsing errors, route to invalid queue

### Dependency Injection
- Uses Google Guice for dependency injection
- Constructor injection preferred
- `@Inject` annotations for dependency resolution

## Important Notes

- All code uses Java 21 features and patterns
- Follows semantic versioning (MAJOR.MINOR.PATCH)
- Update CHANGELOG.md for all changes
- Optimistic locking implemented via version fields on aggregates
- Command and event idempotency built into base classes
- Memento pattern used for aggregate state management