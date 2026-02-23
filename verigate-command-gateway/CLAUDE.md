# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Initial Setup

### First-Time Setup
```bash
# Run the setup script to configure your development environment
./setup.sh
```

The setup script will:
- Install required dependencies (jq, Maven, Docker, Colima)
- Create Maven settings.xml template for GitHub Package Registry access
- Set up Docker environment
- Validate the project configuration

### Manual GitHub Package Registry Setup
After running setup.sh, you must configure GitHub credentials:

1. **Create GitHub Personal Access Token** with `read:packages` scope:
   https://github.com/settings/tokens/new?scopes=read:packages

2. **Edit Maven settings** at `~/.m2/settings.xml`:
   ```xml
   <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
     <servers>
       <server>
         <id>github</id>
         <username>YOUR_GITHUB_USERNAME</username>
         <password>YOUR_GITHUB_PERSONAL_ACCESS_TOKEN</password>
       </server>
     </servers>
   </settings>
   ```

3. **Test the setup**:
   ```bash
   mvn dependency:resolve
   ```

## Build and Test Commands

### Building the Project
```bash
# Build entire multi-module project
mvn clean install

# Build without tests (faster compilation check)
mvn clean compile

# Build specific module (from project root)
mvn clean install -pl src/verigate-command-gateway

# Dependency analysis
mvn dependency:analyze
```

### Running Tests
```bash
# Run all tests across all modules
mvn test

# Run integration tests (includes Testcontainers with LocalStack)
mvn verify

# Run specific test class
mvn test -Dtest=DefaultVerifyPartyCommandHandlerTest

# Run tests for specific module
mvn test -pl src/verigate-adapter-worldcheck

# Skip integration tests (run only unit tests)
mvn test -DskipITs

# Run tests with verbose output
mvn test -X
```

### Code Quality and Linting
```bash
# Checkstyle validation (Google style checks)
mvn checkstyle:check

# Compilation check for syntax errors
mvn clean compile

# Validate project structure and dependencies
mvn validate
```

### Live Integration Testing
```bash
# WorldCheck live API testing (requires credentials)
cd src/verigate-adapter-worldcheck-infrastructure
mvn test -Dtest=WorldcheckLiveIntegrationTest

# CIPC live API testing
cd src/verigate-adapter-cipc-infrastructure  
mvn test -Dtest=CipcLiveIntegrationTest
```

## High-Level Architecture

### Multi-Module Verification Platform
Verigate is a **command gateway-based verification platform** that routes verification requests to pluggable adapters for different verification providers. The system follows **Clean Architecture** principles with **event-driven** communication.

### Command Flow Architecture
```
Client → Command Gateway → Queue Router → Verification Adapter → External API → Event Publisher
```

1. **VerifyPartyCommand** enters via command gateway
2. **Command validation** using specification pattern based on verification type
3. **Queue routing** dispatches commands to appropriate adapter queues
4. **Adapter handlers** process commands and call external verification APIs
5. **Standardized results** returned as VerificationResult with outcomes
6. **Domain events** published based on verification outcomes

### Module Structure
```
src/
├── verigate-command-gateway/           # Central command routing and orchestration
│   ├── verigate-command-gateway-domain/          # Core business logic and events
│   ├── verigate-command-gateway-application/     # Command handlers and use cases
│   └── verigate-command-gateway-infrastructure/  # AWS integration (SQS, Lambda, DynamoDB)
│
├── verigate-adapter-worldcheck/        # Refinitiv WorldCheck integration
│   ├── verigate-adapter-worldcheck-domain/       # WorldCheck business logic
│   ├── verigate-adapter-worldcheck-application/  # Verification use cases
│   └── verigate-adapter-worldcheck-infrastructure/ # WorldCheck API integration
│
└── verigate-adapter-cipc/              # South African CIPC integration
    ├── verigate-adapter-cipc-domain/             # CIPC business logic
    ├── verigate-adapter-cipc-application/        # Company verification use cases
    └── verigate-adapter-cipc-infrastructure/     # CIPC API integration
```

### Core Domain Models

#### Command Gateway Domain
- **`VerifyPartyCommand`** - Central verification command with type, origination, and metadata
- **`VerificationResult`** - Standardized result with outcome and failure details
- **`VerificationOutcome`** - Enum: `SUCCEEDED`, `HARD_FAIL`, `SOFT_FAIL`, `SYSTEM_OUTAGE`
- **`VerificationType`** - Enum: `VERIFICATION_OF_PERSONAL_DETAILS`, `VERIFICATION_OF_BANK_DETAILS`, `SANCTIONS_SCREENING`
- **`VerificationEvent`** hierarchy - Domain events for different outcomes

#### Verification Adapters
- **WorldCheck**: Personal verification, sanctions screening, case management
- **CIPC**: South African company verification, enterprise number validation
- **QLink**: Bank account verification (configured but implementation not shown)

### Event-Driven Architecture
The system publishes domain events for all verification outcomes:
- **`VerificationSucceededEvent`** - Successful verification completion
- **`VerificationHardFailEvent`** - Permanent failures (invalid data, authentication)
- **`VerificationSoftFailEvent`** - Business rule failures or retriable errors
- **`VerificationSystemOutageEvent`** - External service unavailability

### Infrastructure Integration

#### AWS Services
- **SQS** - Queue-based command routing to verification adapters
- **Lambda** - Serverless execution of verification handlers
- **DynamoDB** - Command persistence and audit trail
- **EventBridge/Kinesis** - Event publishing for downstream systems

#### External API Integration
- **WorldCheck Case Management API v3** - Personal verification and sanctions screening
- **CIPC Public API** - South African company registry verification
- **HMAC-SHA256 authentication** for WorldCheck integration
- **Rate limiting and retry logic** with exponential backoff

### Configuration Management
- **Environment-driven configuration** via `application.properties`
- **Feature flags** for disabling verification routing during maintenance
- **Configuration validation** on startup with detailed error reporting
- **Sensitive data masking** in logs and monitoring

### Testing Strategy

#### Multi-Level Testing
- **Unit tests** with JUnit Jupiter and Mockito
- **Integration tests** with Testcontainers and LocalStack for AWS services
- **Live integration tests** with actual external APIs (requires credentials)
- **Mock implementations** for external dependencies during testing

#### Test Configuration
- Test-specific property files with reduced timeouts
- Environment variable documentation in `environment-variables.md`
- Comprehensive mock services for external API simulation

### Development Patterns

#### Clean Architecture Implementation
All modules follow the same layered structure:
- **Domain Layer** - Core business logic, models, interfaces (no external dependencies)
- **Application Layer** - Use case orchestration and command handlers
- **Infrastructure Layer** - External integrations, databases, messaging, HTTP clients

#### Error Handling Strategy
- **`TransientException`** - Retriable errors (network issues, temporary API problems)
- **`PermanentException`** - Non-retriable errors (invalid data, authentication failures)
- **Structured exception mapping** from external API errors to domain exceptions

#### Dependency Injection
- **Google Guice** for dependency injection across all modules
- **Named bindings** for different configurations (real vs. mock services)
- **Factory patterns** for creating queue dispatchers and specifications

### Integration Points

#### Adding New Verification Providers
1. Create new adapter module following the established three-layer structure
2. Implement domain service interfaces for the new provider
3. Add command handlers that integrate with the command gateway
4. Configure queue routing in `DomainConstants.VERIFICATION_TYPE_TO_QUEUE_NAME_MAP`
5. Add appropriate AWS infrastructure (SQS queues, Lambda functions)

#### Command Gateway Extensions
- **Specification pattern** for validation rules based on verification type
- **Queue routing configuration** maps verification types to SQS queue names
- **Event factory pattern** creates appropriate domain events based on outcomes

### Security Considerations
- **No hardcoded credentials** - All sensitive configuration via environment variables
- **HMAC authentication** for API integrations requiring cryptographic signatures
- **Sensitive data masking** in logs and audit trails
- **Feature flags** for emergency disabling of verification providers

### Monitoring and Observability
- **Structured logging** with contextual information and correlation IDs
- **Micrometer metrics** for performance monitoring
- **AWS CloudWatch integration** for operational visibility
- **Domain event publishing** enables comprehensive audit trails