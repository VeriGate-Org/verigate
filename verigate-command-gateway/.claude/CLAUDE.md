# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Test Commands

### Building the Project
```bash
# Build the entire CIPC adapter
mvn clean compile

# Build with all tests
mvn clean install

# Build specific module (from adapter root)
cd verigate-adapter-cipc-infrastructure && mvn clean compile
```

### Running Tests
```bash
# Run all tests in the adapter
mvn test

# Run specific test class
mvn test -Dtest=CipcApiConfigurationTest

# Run specific test from infrastructure module
cd verigate-adapter-cipc-infrastructure && mvn test -Dtest=ClassName

# Run tests with verbose output
mvn test -X

# Run integration tests only
mvn test -Dtest="*IT"
```

### Linting and Code Quality
```bash
# Compile to check for compilation errors
mvn clean compile

# Check for dependency issues
mvn dependency:analyze
```

## High-Level Architecture

### CIPC Adapter Overview
This is the **CIPC Public Data API adapter** for the VeriGate verification platform. It provides company verification capabilities using South Africa's Companies and Intellectual Property Commission (CIPC) public data.

### Clean Architecture Implementation
The adapter follows hexagonal (clean) architecture with three distinct layers:

```
verigate-adapter-cipc/
├── verigate-adapter-cipc-domain/           # Core business logic
├── verigate-adapter-cipc-application/      # Use cases & orchestration  
└── verigate-adapter-cipc-infrastructure/   # External integrations
```

### Key Architectural Components

#### 1. Domain Layer (`-domain/`)
- **Services**: `CipcCompanyService` - interface for company data operations
- **Models**: Comprehensive domain models for CIPC data (`CompanyProfile`, `Director`, `Secretary`, etc.)
- **Mappers**: `CompanyProfileMapper` - business logic for mapping between VeriGate and CIPC formats
- **Constants**: `DomainConstants` - domain-level constants and defaults
- **Enums**: `CompanyStatus`, `DirectorStatus`, `DirectorType` for type safety

#### 2. Application Layer (`-application/`)
- **Command Handlers**: `DefaultVerifyCompanyDetailsCommandHandler` - main entry point for company verification
- **Orchestration**: Coordinates company data retrieval and verification analysis

#### 3. Infrastructure Layer (`-infrastructure/`)
- **HTTP Adapters**: Type-safe API clients (`CipcHttpAdapter`, `CipcCompanyApiAdapter`)
- **Authentication**: Simple API key authentication
- **Configuration**: Environment-driven config (`CipcApiConfiguration`, `ConfigurationValidator`)
- **DTOs**: Complete mapping between CIPC API responses and domain models
- **AWS Integration**: Lambda handlers and dependency injection

### CIPC API Integration Architecture

#### API Key Authentication Flow
The adapter uses simple API key authentication:
1. API key provided via `CIPC_API_KEY` environment variable
2. All HTTP requests include `Ocp-Apim-Subscription-Key` header
3. No complex signature generation required

#### HTTP Client Architecture
```
CipcHttpAdapter (base HTTP client)
└── CipcCompanyApiAdapter (company-specific endpoints)
```

#### Service Implementation Pattern
Each service follows this pattern:
- Interface in domain layer defines business operations
- Default implementation in infrastructure uses HTTP adapter
- Mock implementation in test layer for testing
- Dependency injection wires real vs. mock implementations

### Configuration Architecture

#### Environment-Driven Configuration
Required environment variables:
- `CIPC_API_KEY` - API key for CIPC Public Data API

Optional environment variables:
- `CIPC_BASE_URL` - API base URL (defaults to dev environment)
- `CIPC_HTTP_TIMEOUT_SECONDS` - HTTP timeout configuration
- Rate limiting and logging configuration

#### Configuration Validation
- `ConfigurationValidator` validates required settings on startup
- `CipcApiConfiguration` provides type-safe access to all settings
- Comprehensive defaults in `DomainConstants` and `application.properties`

### Key Integration Points

#### VeriGate Command Gateway Integration
- Implements `VerifyCompanyDetailsCommandHandler` interface
- Receives `VerifyPartyCommand` from command gateway
- Returns `VerificationResult` with standardized outcomes

#### Company Verification Workflow
1. **Command Reception**: Gateway sends `VerifyPartyCommand` with enterprise number
2. **Data Retrieval**: Fetch company profile from CIPC Public Data API
3. **Verification Analysis**: Analyze company status, directors, and compliance
4. **Result Mapping**: Return `VerificationResult` to gateway

### CIPC-Specific Features

#### Enterprise Number Validation
- Validates South African enterprise number format: `YYYY/NNNNNN/NN`
- Example: `2020/939681/07`

#### Company Status Analysis
- **Active Companies**: `ACTIVE`, `IN_BUSINESS` status
- **Inactive Companies**: `DEREGISTERED`, `IN_LIQUIDATION`, etc.
- **Business Rules**: Companies must be active and have active directors to pass verification

#### Director Verification
- Analyzes director status (active, resigned, removed, etc.)
- Checks appointment and resignation dates
- Requires at least one active director for successful verification

### Testing Strategy

#### Mock Architecture
- `MockCipcCompanyService` - provides test scenarios and data
- `TestServiceModule` - wires mock implementations for testing
- Scenario-based testing for different company states

#### Test Configuration
- Test-specific `application.properties` with reduced timeouts
- Environment variables documented in `environment-variables.md`
- Integration tests validate end-to-end flows

### Development Notes

#### Adding New Endpoints
1. Add method to `CipcCompanyService` interface in domain layer
2. Add corresponding DTO classes for request/response
3. Implement in `CipcCompanyApiAdapter` with proper endpoint handling
4. Update `DefaultCipcCompanyService` to use new endpoint
5. Add mock implementation for testing

#### Configuration Changes
1. Add new constants to `EnvironmentConstants` and `DomainConstants`
2. Update `CipcApiConfiguration` with getter methods
3. Add properties to `application.properties` files
4. Update validation in `ConfigurationValidator`
5. Document in `environment-variables.md`

#### Error Handling
The adapter distinguishes between:
- **Transient Errors**: Network timeouts, server errors (5xx) - can be retried
- **Permanent Errors**: Authentication failures, bad requests (4xx) - should not be retried
- **Business Failures**: Company not found, inactive status - return SOFT_FAIL

### API Endpoints Used

The adapter currently uses these CIPC endpoints:
- `/companyprofile` - Complete company profile with all related data
- `/information` - Basic company information (lightweight)

### Rate Limiting
- CIPC has conservative rate limits (5 RPS default)
- Built-in rate limiting configuration
- Exponential backoff for retries

### Security Considerations
- API keys stored in environment variables, never in code
- URL masking in logs to prevent sensitive data exposure
- Proper validation of all inputs (enterprise numbers, etc.)
- No sensitive company data logged

### Future Enhancements
- Additional CIPC endpoints (`/directors`, `/appointments`, etc.)
- Caching for frequently accessed company data
- Enhanced business rules and verification logic
- Bulk company verification capabilities