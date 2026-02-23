# VeriGate BFF (Backend-for-Frontend) Microservice Design

## Executive Summary

This document outlines the design for a **standalone BFF microservice** that serves as the primary interface between frontend applications and VeriGate's verification platform. The BFF follows Domain-Driven Design (DDD) principles, clean architecture patterns, and aligns with VeriGate's existing multi-module hexagonal architecture.

---

## Architecture Overview

### High-Level System Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                     Frontend Applications                             │
│              (Web, Mobile, Partner Portals)                          │
└────────────────────────┬─────────────────────────────────────────────┘
                         │ HTTPS/REST/WebSocket
                         ▼
┌──────────────────────────────────────────────────────────────────────┐
│                  VeriGate BFF Microservice                            │
│                (Spring Boot 3.x on AWS ECS Fargate)                  │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │              API Layer (REST Controllers)                    │   │
│  │  • SanctionsScreeningController                              │   │
│  │  • VerificationStatusController                              │   │
│  │  • VerificationHistoryController                             │   │
│  └────────┬────────────────────────────────────────────────────┘   │
│           │                                                          │
│  ┌────────▼────────────────────────────────────────────────────┐   │
│  │          Application Layer (Use Cases)                       │   │
│  │  • SubmitScreeningUseCase                                    │   │
│  │  • GetScreeningStatusUseCase                                 │   │
│  │  • SearchVerificationHistoryUseCase                          │   │
│  └────────┬────────────────────────────────────────────────────┘   │
│           │                                                          │
│  ┌────────▼────────────────────────────────────────────────────┐   │
│  │              Domain Layer (DDD)                              │   │
│  │  • ScreeningRequest (Aggregate Root)                         │   │
│  │  • ScreeningResult (Value Object)                            │   │
│  │  • ScreeningService (Domain Service)                         │   │
│  │  • Domain Events & Business Rules                            │   │
│  └────────┬────────────────────────────────────────────────────┘   │
│           │                                                          │
│  ┌────────▼────────────────────────────────────────────────────┐   │
│  │         Infrastructure Layer (Adapters)                      │   │
│  │  • SqsCommandPublisher → Command Gateway                     │   │
│  │  • KinesisEventConsumer ← Verification Events                │   │
│  │  • DynamoDbRepository (Persistence)                          │   │
│  │  • RedisCacheService (Performance)                           │   │
│  └────────┬────────────────────────────────────────────────────┘   │
└───────────┼──────────────────────────────────────────────────────────┘
            │
   ┌────────┼─────────┬─────────────┬──────────────┐
   │        │         │             │              │
   ▼        ▼         ▼             ▼              ▼
┌─────┐  ┌──────┐ ┌────────┐  ┌────────┐    ┌────────┐
│ SQS │  │Kinesis│ │DynamoDB│  │ Redis  │    │   S3   │
└──┬──┘  └───┬──┘ └────────┘  └────────┘    └────────┘
   │         │
   ▼         ▼
┌────────────────────────────────────────────────────────┐
│       VeriGate Command Gateway (Existing Lambda)        │
│  • Routes VerifyPartyCommand to adapters                │
│  • Publishes verification events to Kinesis             │
└────────────┬───────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────────┐
│         Verification Adapters (Existing)                │
│  • OpenSanctions, WorldCheck, CIPC, DeedsWeb           │
└────────────────────────────────────────────────────────┘
```

### Request Flow Patterns

#### Pattern 1: Asynchronous Submission (Recommended)

```
1. Frontend → BFF: POST /api/v1/screenings
   {
     "type": "SANCTIONS_SCREENING",
     "entity": { "name": "John Smith", ... }
   }

2. BFF validates request → Creates ScreeningRequest aggregate

3. BFF → SQS: Publishes VerifyPartyCommand
   
4. BFF → Frontend: Returns 202 Accepted
   {
     "requestId": "req_abc123",
     "status": "SUBMITTED",
     "statusUrl": "/api/v1/screenings/req_abc123/status"
   }

5. Frontend polls status endpoint OR subscribes to WebSocket

6. Command Gateway processes command → Adapter executes → Event published

7. BFF consumes Kinesis event → Updates cache/DB → Notifies frontend (WebSocket)

8. Frontend → BFF: GET /api/v1/screenings/req_abc123/result
   Returns completed screening results
```

---

## Why BFF vs Direct Lambda API?

### Comparison Matrix

| Capability | BFF Microservice | Direct Lambda API |
|-----------|------------------|-------------------|
| **DDD/Clean Architecture** | ✅ Perfect fit | ⚠️ Limited support |
| **Complex Business Logic** | ✅ Rich domain modeling | ❌ Constrained |
| **State Management** | ✅ In-memory, Redis, sessions | ❌ Requires external state |
| **Request Orchestration** | ✅ Native Spring/Java | ⚠️ Needs Step Functions |
| **WebSocket Support** | ✅ Native Spring WebSocket | ⚠️ API Gateway WebSocket |
| **Developer Experience** | ✅ Traditional Spring Boot | ⚠️ AWS-specific knowledge |
| **Local Development** | ✅ Easy with Docker Compose | ⚠️ LocalStack required |
| **Testing** | ✅ Standard JUnit/TestContainers | ⚠️ Lambda testing tools |
| **Cost (predictable traffic)** | ⚠️ ~$95/month fixed | ✅ ~$19/month variable |
| **Cold Start Latency** | ✅ Always warm | ❌ 1-3 seconds |
| **Scalability** | ✅ Auto-scaling with ALB | ✅ Auto-scaling |
| **Deployment** | ⚠️ Container deployment | ✅ Serverless |

### Recommended Hybrid Architecture

**Use BFF for:**
- Frontend-facing API (this BFF microservice)
- Complex orchestration logic
- Real-time updates (WebSocket)
- Request aggregation
- Caching and state management

**Keep Lambda for:**
- Backend command processing (existing Command Gateway)
- Integration with external APIs (existing adapters)
- Event-driven processing
- Cost-effective async workloads

**Result:** Best of both worlds!

---

## Module Structure (Clean Architecture)

Following VeriGate's established pattern:

```
verigate-bff/
├── pom.xml                               # Parent POM
├── README.md
├── CLAUDE.md
│
├── verigate-bff-domain/                  # Pure business logic
│   ├── pom.xml
│   └── src/main/java/verigate/bff/domain/
│       ├── models/
│       │   ├── ScreeningRequest.java          # Aggregate Root
│       │   ├── ScreeningRequestId.java        # Value Object (ID)
│       │   ├── ScreeningStatus.java           # Enum
│       │   ├── ScreeningResult.java           # Value Object
│       │   ├── EntityDetails.java             # Value Object
│       │   ├── PersonalDetails.java           # Value Object
│       │   ├── Match.java                     # Value Object
│       │   └── RiskAssessment.java            # Value Object
│       ├── commands/
│       │   ├── SubmitScreeningCommand.java
│       │   └── CancelScreeningCommand.java
│       ├── events/
│       │   ├── ScreeningSubmittedEvent.java
│       │   ├── ScreeningCompletedEvent.java
│       │   └── ScreeningFailedEvent.java
│       ├── services/
│       │   ├── ScreeningService.java          # Domain Service
│       │   └── VerificationOrchestrator.java
│       ├── repositories/                      # Interfaces (Ports)
│       │   ├── ScreeningRequestRepository.java
│       │   └── VerificationHistoryRepository.java
│       ├── publishers/                        # Interfaces (Ports)
│       │   ├── CommandPublisher.java
│       │   └── DomainEventPublisher.java
│       └── exceptions/
│           ├── InvalidScreeningRequestException.java
│           ├── ScreeningNotFoundException.java
│           └── ScreeningBusinessException.java
│
├── verigate-bff-application/             # Use cases
│   ├── pom.xml
│   └── src/main/java/verigate/bff/application/
│       ├── usecases/
│       │   ├── SubmitScreeningUseCase.java
│       │   ├── GetScreeningStatusUseCase.java
│       │   ├── GetScreeningResultUseCase.java
│       │   ├── SearchScreeningHistoryUseCase.java
│       │   └── CancelScreeningUseCase.java
│       ├── dto/
│       │   ├── ScreeningRequestDto.java
│       │   ├── ScreeningResultDto.java
│       │   ├── ScreeningStatusDto.java
│       │   └── PageDto.java
│       ├── mappers/
│       │   ├── ScreeningMapper.java
│       │   └── EntityMapper.java
│       └── handlers/
│           ├── VerificationCompletedEventHandler.java
│           └── VerificationFailedEventHandler.java
│
└── verigate-bff-infrastructure/          # External integrations
    ├── pom.xml
    ├── Dockerfile
    └── src/main/java/verigate/bff/infrastructure/
        ├── api/
        │   ├── rest/
        │   │   ├── controllers/
        │   │   │   ├── SanctionsScreeningController.java
        │   │   │   ├── VerificationController.java
        │   │   │   ├── HealthController.java
        │   │   │   └── WebSocketController.java
        │   │   ├── request/
        │   │   │   ├── ScreenEntityRequest.java
        │   │   │   └── SearchRequest.java
        │   │   ├── response/
        │   │   │   ├── ScreeningSubmissionResponse.java
        │   │   │   ├── StatusResponse.java
        │   │   │   ├── ResultResponse.java
        │   │   │   └── ErrorResponse.java
        │   │   └── validation/
        │   │       └── ScreeningRequestValidator.java
        │   └── websocket/
        │       ├── ScreeningWebSocketHandler.java
        │       └── WebSocketConfiguration.java
        ├── messaging/
        │   ├── sqs/
        │   │   ├── SqsCommandPublisher.java
        │   │   └── SqsConfiguration.java
        │   └── kinesis/
        │       ├── KinesisEventConsumer.java
        │       ├── KinesisEventProcessor.java
        │       └── KinesisConfiguration.java
        ├── persistence/
        │   ├── dynamodb/
        │   │   ├── DynamoDbScreeningRepository.java
        │   │   ├── DynamoDbVerificationHistoryRepository.java
        │   │   ├── entities/
        │   │   │   ├── ScreeningRequestEntity.java
        │   │   │   └── VerificationHistoryEntity.java
        │   │   └── DynamoDbConfiguration.java
        │   └── redis/
        │       ├── RedisScreeningCache.java
        │       ├── RedisCacheConfiguration.java
        │       └── RedisKeyGenerator.java
        ├── security/
        │   ├── JwtAuthenticationFilter.java
        │   ├── JwtTokenProvider.java
        │   ├── ApiKeyAuthenticationFilter.java
        │   └── SecurityConfiguration.java
        ├── config/
        │   ├── ApplicationConfiguration.java
        │   ├── AwsConfiguration.java
        │   ├── CorsConfiguration.java
        │   └── ObservabilityConfiguration.java
        └── Application.java                   # Spring Boot entry point
```

---

## Domain Model Design (DDD)

### Bounded Context: Verification Frontend Experience

The BFF represents a **distinct bounded context** focused on frontend verification workflows, separate from the backend verification processing context.

### Core Aggregate: ScreeningRequest

```java
package verigate.bff.domain.models;

import java.time.Instant;
import java.util.*;

/**
 * Aggregate root for screening requests submitted from the frontend.
 * Encapsulates all business rules and invariants for screening lifecycle.
 */
public class ScreeningRequest {
  
  private final ScreeningRequestId id;
  private final UserId userId;
  private final ScreeningType type;
  private final EntityDetails entity;
  private final ScreeningOptions options;
  private ScreeningStatus status;
  private ScreeningResult result;
  private final Instant createdAt;
  private Instant updatedAt;
  private final List<DomainEvent> domainEvents;
  
  // Private constructor - use Builder
  private ScreeningRequest(Builder builder) {
    this.id = ScreeningRequestId.generate();
    this.userId = Objects.requireNonNull(builder.userId, "userId cannot be null");
    this.type = Objects.requireNonNull(builder.type, "type cannot be null");
    this.entity = Objects.requireNonNull(builder.entity, "entity cannot be null");
    this.options = builder.options != null ? builder.options : ScreeningOptions.defaults();
    this.status = ScreeningStatus.PENDING;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.domainEvents = new ArrayList<>();
    
    // Validate business rules
    validate();
  }
  
  /**
   * Submit the screening request for processing.
   * Business Rule: Can only submit if status is PENDING.
   */
  public void submit() {
    if (this.status != ScreeningStatus.PENDING) {
      throw new IllegalStateException(
          "Cannot submit screening in status: " + this.status);
    }
    
    this.status = ScreeningStatus.SUBMITTED;
    this.updatedAt = Instant.now();
    
    addDomainEvent(new ScreeningSubmittedEvent(
        this.id, this.type, this.userId, this.createdAt));
  }
  
  /**
   * Mark screening as in progress.
   */
  public void markInProgress() {
    if (this.status != ScreeningStatus.SUBMITTED) {
      throw new IllegalStateException(
          "Cannot mark in-progress from status: " + this.status);
    }
    
    this.status = ScreeningStatus.IN_PROGRESS;
    this.updatedAt = Instant.now();
  }
  
  /**
   * Complete the screening with results.
   * Business Rule: Must be in IN_PROGRESS state to complete.
   */
  public void complete(ScreeningResult result) {
    Objects.requireNonNull(result, "result cannot be null");
    
    if (this.status != ScreeningStatus.IN_PROGRESS) {
      throw new IllegalStateException(
          "Cannot complete screening from status: " + this.status);
    }
    
    this.result = result;
    this.status = ScreeningStatus.COMPLETED;
    this.updatedAt = Instant.now();
    
    addDomainEvent(new ScreeningCompletedEvent(
        this.id, result.getOutcome(), result.getRiskLevel()));
  }
  
  /**
   * Mark screening as failed with reason.
   */
  public void markFailed(FailureReason reason) {
    Objects.requireNonNull(reason, "failure reason cannot be null");
    
    this.status = ScreeningStatus.FAILED;
    this.updatedAt = Instant.now();
    
    addDomainEvent(new ScreeningFailedEvent(this.id, reason));
  }
  
  /**
   * Cancel the screening request.
   * Business Rule: Cannot cancel if already completed or failed.
   */
  public void cancel(UserId cancelledBy) {
    if (this.status == ScreeningStatus.COMPLETED || 
        this.status == ScreeningStatus.FAILED) {
      throw new IllegalStateException(
          "Cannot cancel screening in status: " + this.status);
    }
    
    this.status = ScreeningStatus.CANCELLED;
    this.updatedAt = Instant.now();
    
    addDomainEvent(new ScreeningCancelledEvent(this.id, cancelledBy));
  }
  
  /**
   * Check if screening requires manual review.
   * Business Rule: High/Critical risk requires review.
   */
  public boolean requiresReview() {
    return this.result != null && this.result.requiresReview();
  }
  
  private void validate() {
    if (!entity.isValid()) {
      throw new InvalidScreeningRequestException("Invalid entity details");
    }
    
    // Type-specific validation
    if (type == ScreeningType.SANCTIONS_SCREENING) {
      validateSanctionsScreening();
    }
  }
  
  private void validateSanctionsScreening() {
    if (entity.getType() == EntityType.PERSON) {
      PersonalDetails personal = entity.getPersonal();
      if (personal == null || !personal.hasMinimumRequiredFields()) {
        throw new InvalidScreeningRequestException(
            "Person sanctions screening requires name and at least one identifier");
      }
    }
  }
  
  // Builder pattern
  public static class Builder {
    private UserId userId;
    private ScreeningType type;
    private EntityDetails entity;
    private ScreeningOptions options;
    
    public Builder userId(UserId userId) {
      this.userId = userId;
      return this;
    }
    
    public Builder type(ScreeningType type) {
      this.type = type;
      return this;
    }
    
    public Builder entity(EntityDetails entity) {
      this.entity = entity;
      return this;
    }
    
    public Builder options(ScreeningOptions options) {
      this.options = options;
      return this;
    }
    
    public ScreeningRequest build() {
      return new ScreeningRequest(this);
    }
  }
  
  // Getters
  public ScreeningRequestId getId() { return id; }
  public UserId getUserId() { return userId; }
  public ScreeningType getType() { return type; }
  public EntityDetails getEntity() { return entity; }
  public ScreeningStatus getStatus() { return status; }
  public Optional<ScreeningResult> getResult() { return Optional.ofNullable(result); }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  
  // Domain events
  public List<DomainEvent> getDomainEvents() { 
    return Collections.unmodifiableList(domainEvents); 
  }
  
  public void clearDomainEvents() {
    this.domainEvents.clear();
  }
  
  private void addDomainEvent(DomainEvent event) {
    this.domainEvents.add(event);
  }
}
```

### Value Objects

```java
// ScreeningResult.java
public record ScreeningResult(
    VerificationOutcome outcome,
    RiskLevel riskLevel,
    List<Match> matches,
    ProcessingMetadata metadata,
    Instant completedAt
) {
  
  public ScreeningResult {
    Objects.requireNonNull(outcome, "outcome cannot be null");
    Objects.requireNonNull(riskLevel, "riskLevel cannot be null");
    matches = matches != null ? List.copyOf(matches) : List.of();
    Objects.requireNonNull(completedAt, "completedAt cannot be null");
  }
  
  public boolean isClean() {
    return outcome == VerificationOutcome.SUCCEEDED && 
           riskLevel == RiskLevel.LOW;
  }
  
  public boolean requiresReview() {
    return riskLevel == RiskLevel.MEDIUM || 
           riskLevel == RiskLevel.HIGH ||
           riskLevel == RiskLevel.CRITICAL;
  }
  
  public boolean isCritical() {
    return riskLevel == RiskLevel.CRITICAL;
  }
  
  public int getMatchCount() {
    return matches.size();
  }
  
  public List<Match> getHighConfidenceMatches() {
    return matches.stream()
        .filter(Match::isHighConfidence)
        .toList();
  }
}

// Match.java
public record Match(
    String entityId,
    double score,
    String caption,
    String schema,
    List<String> datasets,
    Map<String, List<String>> properties,
    List<String> topics,
    RiskLevel riskLevel
) {
  
  public Match {
    if (score < 0.0 || score > 1.0) {
      throw new IllegalArgumentException("Score must be between 0 and 1");
    }
    datasets = datasets != null ? List.copyOf(datasets) : List.of();
    properties = properties != null ? Map.copyOf(properties) : Map.of();
    topics = topics != null ? List.copyOf(topics) : List.of();
  }
  
  public boolean isHighConfidence() {
    return score >= 0.9;
  }
  
  public boolean isMediumConfidence() {
    return score >= 0.7 && score < 0.9;
  }
  
  public boolean isLowConfidence() {
    return score >= 0.5 && score < 0.7;
  }
}

// EntityDetails.java
public record EntityDetails(
    EntityType type,
    PersonalDetails personal,
    BusinessDetails business,
    Map<String, Object> additionalData
) {
  
  public EntityDetails {
    Objects.requireNonNull(type, "type cannot be null");
    additionalData = additionalData != null ? Map.copyOf(additionalData) : Map.of();
  }
  
  public boolean isValid() {
    return switch (type) {
      case PERSON -> personal != null && personal.isValid();
      case COMPANY, ORGANIZATION -> business != null && business.isValid();
    };
  }
}

// PersonalDetails.java
public record PersonalDetails(
    String firstName,
    String lastName,
    LocalDate birthDate,
    String nationality,
    String idNumber
) {
  
  public PersonalDetails {
    Objects.requireNonNull(firstName, "firstName cannot be null");
    Objects.requireNonNull(lastName, "lastName cannot be null");
  }
  
  public String fullName() {
    return firstName + " " + lastName;
  }
  
  public boolean isValid() {
    return !firstName.isBlank() && !lastName.isBlank();
  }
  
  public boolean hasMinimumRequiredFields() {
    return isValid() && (birthDate != null || nationality != null || idNumber != null);
  }
}
```

### Domain Service

```java
package verigate.bff.domain.services;

/**
 * Domain service coordinating screening request processing.
 * Contains business logic that doesn't naturally fit in an aggregate.
 */
@Service
public class ScreeningService {
  
  private final ScreeningRequestRepository repository;
  private final CommandPublisher commandPublisher;
  private final DomainEventPublisher eventPublisher;
  
  /**
   * Submit a new screening request for processing.
   * Orchestrates aggregate creation, persistence, and command publishing.
   */
  @Transactional
  public ScreeningRequestId submitScreeningRequest(
      UserId userId,
      ScreeningType type,
      EntityDetails entity,
      ScreeningOptions options) {
    
    log.info("Submitting screening request: userId={}, type={}", userId, type);
    
    // Create aggregate
    ScreeningRequest request = new ScreeningRequest.Builder()
        .userId(userId)
        .type(type)
        .entity(entity)
        .options(options)
        .build();
    
    // Apply business logic
    request.submit();
    
    // Persist
    repository.save(request);
    
    // Publish command to backend (via SQS)
    VerifyPartyCommand command = buildVerifyPartyCommand(request);
    commandPublisher.publishVerifyPartyCommand(command);
    
    // Publish domain events
    publishDomainEvents(request);
    
    log.info("Screening request submitted: requestId={}", request.getId());
    
    return request.getId();
  }
  
  /**
   * Handle verification completed event from backend.
   */
  @Transactional
  public void handleVerificationCompleted(
      ScreeningRequestId requestId,
      VerificationResult backendResult) {
    
    log.info("Handling verification completed: requestId={}", requestId);
    
    ScreeningRequest request = repository.findById(requestId)
        .orElseThrow(() -> new ScreeningNotFoundException(requestId));
    
    // Map backend result to domain model
    ScreeningResult result = mapToScreeningResult(backendResult);
    
    // Complete the screening
    request.complete(result);
    
    // Persist
    repository.save(request);
    
    // Publish domain events
    publishDomainEvents(request);
    
    log.info("Verification completed successfully: requestId={}", requestId);
  }
  
  /**
   * Handle verification failed event from backend.
   */
  @Transactional
  public void handleVerificationFailed(
      ScreeningRequestId requestId,
      FailureReason reason) {
    
    log.info("Handling verification failed: requestId={}, reason={}", 
        requestId, reason);
    
    ScreeningRequest request = repository.findById(requestId)
        .orElseThrow(() -> new ScreeningNotFoundException(requestId));
    
    request.markFailed(reason);
    repository.save(request);
    publishDomainEvents(request);
  }
  
  private VerifyPartyCommand buildVerifyPartyCommand(ScreeningRequest request) {
    return new VerifyPartyCommand(
        UUID.randomUUID(),
        Instant.now(),
        request.getUserId().value(),
        mapToVerificationType(request.getType()),
        new Origination("BFF", "verigate-bff-service"),
        buildMetadata(request.getEntity(), request.getOptions())
    );
  }
  
  private void publishDomainEvents(ScreeningRequest request) {
    request.getDomainEvents().forEach(eventPublisher::publish);
    request.clearDomainEvents();
  }
  
  // Mapping methods...
}
```

---

## Technology Stack

### Framework: Spring Boot 3.x (Recommended)

**Why Spring Boot:**
- ✅ Mature ecosystem with excellent DDD/clean architecture support
- ✅ Seamless AWS SDK v2 integration
- ✅ Rich security features (Spring Security)
- ✅ Comprehensive testing support
- ✅ Easy containerization and deployment
- ✅ Large community and extensive documentation

### Core Dependencies

```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
    <aws-sdk.version>2.28.23</aws-sdk.version>
</properties>

<dependencies>
    <!-- Spring Boot Core -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- AWS Integration -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>sqs</artifactId>
    </dependency>
    
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>kinesis</artifactId>
    </dependency>
    
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>dynamodb-enhanced</artifactId>
    </dependency>
    
    <!-- Caching -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- WebSocket -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    
    <!-- Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    
    <!-- Observability -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-cloudwatch2</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>localstack</artifactId>
        <version>1.20.4</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## REST API Design

### Endpoint Specification

```
POST   /api/v1/screenings                 Submit new screening
GET    /api/v1/screenings/{id}            Get screening details
GET    /api/v1/screenings/{id}/status     Get screening status
GET    /api/v1/screenings/{id}/result     Get screening result
DELETE /api/v1/screenings/{id}            Cancel screening
GET    /api/v1/screenings                 List user's screenings (paginated)

GET    /actuator/health                   Health check
GET    /actuator/metrics                  Metrics
```

### Example: Submit Screening

**Request:**
```http
POST /api/v1/screenings HTTP/1.1
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "type": "SANCTIONS_SCREENING",
  "entity": {
    "type": "PERSON",
    "personal": {
      "firstName": "John",
      "lastName": "Smith",
      "birthDate": "1980-05-15",
      "nationality": "US",
      "idNumber": "123-45-6789"
    }
  },
  "options": {
    "datasets": ["sanctions", "pep"],
    "threshold": 0.7,
    "includeMatches": true
  }
}
```

**Response (202 Accepted):**
```json
{
  "requestId": "req_abc123xyz",
  "status": "SUBMITTED",
  "statusUrl": "/api/v1/screenings/req_abc123xyz/status",
  "submittedAt": "2025-01-15T10:30:00Z",
  "estimatedCompletion": "2025-01-15T10:30:30Z"
}
```

### Controller Implementation

```java
@RestController
@RequestMapping("/api/v1/screenings")
@RequiredArgsConstructor
@Validated
public class SanctionsScreeningController {
  
  private final SubmitScreeningUseCase submitUseCase;
  private final GetScreeningStatusUseCase getStatusUseCase;
  private final GetScreeningResultUseCase getResultUseCase;
  
  @PostMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ScreeningSubmissionResponse submitScreening(
      @Valid @RequestBody ScreenEntityRequest request,
      @AuthenticationPrincipal UserDetails user) {
    
    log.info("Received screening request from user: {}", user.getUsername());
    
    ScreeningRequestId requestId = submitUseCase.execute(
        new SubmitScreeningCommand(
            UserId.of(user.getUsername()),
            ScreeningType.valueOf(request.getType()),
            mapToEntityDetails(request.getEntity()),
            mapToOptions(request.getOptions())
        )
    );
    
    return ScreeningSubmissionResponse.builder()
        .requestId(requestId.value())
        .status("SUBMITTED")
        .statusUrl("/api/v1/screenings/" + requestId.value() + "/status")
        .submittedAt(Instant.now())
        .estimatedCompletion(Instant.now().plus(Duration.ofSeconds(30)))
        .build();
  }
  
  @GetMapping("/{requestId}/status")
  public StatusResponse getStatus(
      @PathVariable String requestId,
      @AuthenticationPrincipal UserDetails user) {
    
    ScreeningStatusDto status = getStatusUseCase.execute(
        new GetScreeningStatusQuery(
            ScreeningRequestId.of(requestId),
            UserId.of(user.getUsername())
        )
    );
    
    return StatusResponse.builder()
        .requestId(requestId)
        .status(status.getStatus().toString())
        .updatedAt(status.getUpdatedAt())
        .message(status.getMessage())
        .progress(status.getProgress())
        .build();
  }
  
  @GetMapping("/{requestId}/result")
  public ResultResponse getResult(
      @PathVariable String requestId,
      @AuthenticationPrincipal UserDetails user) {
    
    ScreeningResultDto result = getResultUseCase.execute(
        new GetScreeningResultQuery(
            ScreeningRequestId.of(requestId),
            UserId.of(user.getUsername())
        )
    );
    
    if (result.getStatus() != ScreeningStatus.COMPLETED) {
      throw new ScreeningNotCompletedException(
          "Screening not completed: " + result.getStatus());
    }
    
    return mapToResultResponse(result);
  }
  
  @ExceptionHandler(ScreeningNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(ScreeningNotFoundException ex) {
    return new ErrorResponse(
        "SCREENING_NOT_FOUND",
        ex.getMessage(),
        Instant.now()
    );
  }
  
  @ExceptionHandler(InvalidScreeningRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleInvalidRequest(InvalidScreeningRequestException ex) {
    return new ErrorResponse(
        "INVALID_REQUEST",
        ex.getMessage(),
        Instant.now()
    );
  }
}
```

---

## Infrastructure Integration

### SQS Command Publisher

```java
@Service
@RequiredArgsConstructor
public class SqsCommandPublisher implements CommandPublisher {
  
  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;
  
  @Value("${aws.sqs.verify-party-queue-url}")
  private String queueUrl;
  
  @Override
  public void publishVerifyPartyCommand(VerifyPartyCommand command) {
    try {
      String messageBody = objectMapper.writeValueAsString(command);
      
      SendMessageRequest request = SendMessageRequest.builder()
          .queueUrl(queueUrl)
          .messageBody(messageBody)
          .messageAttributes(Map.of(
              "CommandType", stringAttribute("VerifyPartyCommand"),
              "VerificationType", stringAttribute(command.getVerificationType().name())
          ))
          .build();
      
      SendMessageResponse response = sqsClient.sendMessage(request);
      
      log.info("Published command: messageId={}, commandId={}", 
          response.messageId(), command.getId());
          
    } catch (Exception e) {
      log.error("Failed to publish command", e);
      throw new CommandPublishingException("Failed to publish command", e);
    }
  }
  
  private MessageAttributeValue stringAttribute(String value) {
    return MessageAttributeValue.builder()
        .dataType("String")
        .stringValue(value)
        .build();
  }
}
```

### Kinesis Event Consumer

```java
@Service
@RequiredArgsConstructor
public class KinesisEventProcessor {
  
  private final ScreeningService screeningService;
  private final ObjectMapper objectMapper;
  
  @KinesisListener(stream = "${aws.kinesis.stream-name}")
  public void processEvent(Record record) {
    try {
      String data = StandardCharsets.UTF_8.decode(record.data()).toString();
      JsonNode event = objectMapper.readTree(data);
      
      String eventType = event.get("eventType").asText();
      
      switch (eventType) {
        case "VerificationSucceeded" -> handleSuccess(event);
        case "VerificationHardFail", "VerificationSoftFail" -> handleFailure(event);
        default -> log.warn("Unknown event type: {}", eventType);
      }
      
    } catch (Exception e) {
      log.error("Error processing Kinesis event", e);
      // Could publish to DLQ here
    }
  }
  
  private void handleSuccess(JsonNode event) {
    ScreeningRequestId requestId = extractRequestId(event);
    VerificationResult result = extractResult(event);
    
    screeningService.handleVerificationCompleted(requestId, result);
  }
  
  private void handleFailure(JsonNode event) {
    ScreeningRequestId requestId = extractRequestId(event);
    FailureReason reason = extractFailureReason(event);
    
    screeningService.handleVerificationFailed(requestId, reason);
  }
}
```

---

## Deployment

### Docker Configuration

```dockerfile
# Dockerfile
FROM amazoncorretto:21-alpine AS builder

WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM amazoncorretto:21-alpine

WORKDIR /app
COPY --from=builder /app/verigate-bff-infrastructure/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx1024m -Xms512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### AWS ECS Task Definition

```json
{
  "family": "verigate-bff",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "1024",
  "memory": "2048",
  "containerDefinitions": [{
    "name": "verigate-bff",
    "image": "${ECR_REPOSITORY}:${VERSION}",
    "portMappings": [{"containerPort": 8080, "protocol": "tcp"}],
    "environment": [
      {"name": "SPRING_PROFILES_ACTIVE", "value": "prod"},
      {"name": "AWS_REGION", "value": "us-east-1"}
    ],
    "secrets": [
      {
        "name": "OPENSANCTIONS_API_KEY",
        "valueFrom": "arn:aws:ssm:::parameter/verigate/opensanctions/api-key"
      }
    ],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "/ecs/verigate-bff",
        "awslogs-region": "us-east-1",
        "awslogs-stream-prefix": "ecs"
      }
    },
    "healthCheck": {
      "command": ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"],
      "interval": 30,
      "timeout": 5,
      "retries": 3
    }
  }]
}
```

---

## Cost Estimation

### Monthly Costs (1M Requests)

| Component | Configuration | Monthly Cost |
|-----------|---------------|--------------|
| ECS Fargate | 2 tasks, 1vCPU/2GB, 24/7 | $50.00 |
| Application Load Balancer | Standard | $16.00 |
| Redis (ElastiCache) | cache.t3.micro | $12.00 |
| DynamoDB | On-demand, 1M writes/10M reads | $2.00 |
| Data Transfer | 100GB outbound | $9.00 |
| CloudWatch Logs | 10GB | $5.00 |
| **Total** | | **~$94/month** |

**Optimization:** With Savings Plans: **~$65-70/month**

---

## Comparison: BFF vs Lambda API

| Aspect | BFF (Spring Boot) | Lambda API |
|--------|-------------------|------------|
| **Cost** | ~$95/month | ~$19/month |
| **Latency (p50)** | < 100ms | < 500ms |
| **Latency (p99)** | < 300ms | < 5s (cold starts) |
| **DDD Support** | ✅ Excellent | ⚠️ Limited |
| **Development** | ✅ Traditional | ⚠️ AWS-specific |
| **Scalability** | ✅ Excellent | ✅ Excellent |
| **State Management** | ✅ Easy | ❌ Difficult |

**Recommendation:** BFF for rich domain logic + predictable traffic.

---

## Implementation Roadmap

### Phase 1: Foundation (Weeks 1-2)
- ✅ Project structure setup
- ✅ Domain models
- ✅ Basic REST API
- ✅ SQS integration

### Phase 2: Integration (Week 3)
- ✅ Kinesis event consumption
- ✅ DynamoDB persistence
- ✅ End-to-end flow

### Phase 3: Performance (Week 4)
- ✅ Redis caching
- ✅ Rate limiting
- ✅ Optimization

### Phase 4: Real-time (Week 5)
- ✅ WebSocket support
- ✅ Push notifications

### Phase 5: Production (Week 6)
- ✅ Security
- ✅ Monitoring
- ✅ Deployment

---

## Conclusion

The BFF microservice provides a **superior architectural approach** for VeriGate's frontend needs:

✅ **Perfect DDD alignment** with clear bounded contexts  
✅ **Clean architecture** maintaining separation of concerns  
✅ **Rich domain modeling** with aggregates and value objects  
✅ **Excellent developer experience** with Spring Boot  
✅ **Production-ready** with comprehensive observability  

The hybrid architecture—BFF for frontend + Lambda for backend processing—delivers the best balance of developer experience, architectural purity, and cost-effectiveness.
