# OpenSanctions Frontend API Design

## Executive Summary

This document outlines recommended approaches for exposing OpenSanctions sanctions screening endpoints to frontend applications, aligned with VeriGate's existing AWS Lambda and queue-based architecture.

## Current Architecture Overview

VeriGate currently uses a **serverless, queue-based architecture** with:
- **AWS Lambda** functions triggered by SQS queues
- **Command Gateway** pattern for routing verification requests
- **Event-driven** outcomes published to Kinesis/EventBridge
- **No direct HTTP API** for synchronous frontend requests

## Recommended Approaches

### Option 1: API Gateway + Lambda (RESTful API) - **RECOMMENDED**

This approach adds a synchronous HTTP layer on top of the existing queue-based architecture, ideal for frontend interactions.

#### Architecture
```
Frontend → API Gateway → Lambda → OpenSanctions Service → Response
                ↓
            DynamoDB (audit)
                ↓
            Kinesis (events)
```

#### Benefits
- ✅ Synchronous responses for better UX
- ✅ Leverages existing Lambda infrastructure
- ✅ Built-in authentication (API Keys, Cognito, IAM)
- ✅ Rate limiting and throttling
- ✅ CORS support out of the box
- ✅ CloudWatch logging and monitoring
- ✅ API versioning support

#### Implementation Steps

##### 1. Create REST API Lambda Handlers

Create a new module: `verigate-adapter-opensanctions-api`

**Directory Structure:**
```
verigate-adapter-opensanctions-api/
├── src/main/java/verigate/adapter/opensanctions/api/
│   ├── handlers/
│   │   ├── SanctionsMatchApiHandler.java
│   │   ├── SanctionsSearchApiHandler.java
│   │   └── SanctionsHealthCheckApiHandler.java
│   ├── models/
│   │   ├── request/
│   │   │   ├── SanctionsMatchRequest.java
│   │   │   └── SanctionsSearchRequest.java
│   │   └── response/
│   │       ├── SanctionsMatchResponse.java
│   │       ├── SanctionsSearchResponse.java
│   │       └── ApiErrorResponse.java
│   └── validation/
│       └── RequestValidator.java
```

**Sample Handler Implementation:**

```java
package verigate.adapter.opensanctions.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import verigate.adapter.opensanctions.api.models.request.SanctionsMatchRequest;
import verigate.adapter.opensanctions.api.models.response.SanctionsMatchResponse;
import verigate.adapter.opensanctions.domain.services.OpenSanctionsMatchingService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * API Gateway Lambda handler for sanctions entity matching.
 * 
 * Endpoint: POST /api/v1/sanctions/match
 */
public class SanctionsMatchApiHandler 
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private static final Logger LOGGER = Logger.getLogger(SanctionsMatchApiHandler.class.getName());
  private static final ObjectMapper MAPPER = new ObjectMapper();
  
  private final OpenSanctionsMatchingService matchingService;
  
  public SanctionsMatchApiHandler() {
    // Initialize with dependency injection
    this.matchingService = DependencyFactory.getMatchingService();
  }
  
  @Override
  public APIGatewayProxyResponseEvent handleRequest(
      APIGatewayProxyRequestEvent input, 
      Context context) {
    
    LOGGER.info("Received sanctions match request");
    
    try {
      // Parse request
      SanctionsMatchRequest request = MAPPER.readValue(
          input.getBody(), 
          SanctionsMatchRequest.class
      );
      
      // Validate request
      validateRequest(request);
      
      // Convert to domain request
      EntityMatchRequest domainRequest = mapToDomainRequest(request);
      
      // Execute matching
      EntityMatchResponse domainResponse = matchingService.matchEntities(domainRequest);
      
      // Convert to API response
      SanctionsMatchResponse apiResponse = mapToApiResponse(domainResponse);
      
      // Audit log (async)
      auditLog(request, apiResponse, context);
      
      // Return success response
      return createResponse(200, apiResponse);
      
    } catch (ValidationException e) {
      LOGGER.warning("Validation error: " + e.getMessage());
      return createErrorResponse(400, "Invalid request", e.getMessage());
      
    } catch (PermanentException e) {
      LOGGER.severe("Permanent error: " + e.getMessage());
      return createErrorResponse(422, "Processing error", e.getMessage());
      
    } catch (TransientException e) {
      LOGGER.warning("Transient error: " + e.getMessage());
      return createErrorResponse(503, "Service temporarily unavailable", e.getMessage());
      
    } catch (Exception e) {
      LOGGER.severe("Unexpected error: " + e.getMessage());
      return createErrorResponse(500, "Internal server error", "An unexpected error occurred");
    }
  }
  
  private APIGatewayProxyResponseEvent createResponse(int statusCode, Object body) {
    try {
      return new APIGatewayProxyResponseEvent()
          .withStatusCode(statusCode)
          .withHeaders(getCorsHeaders())
          .withBody(MAPPER.writeValueAsString(body));
    } catch (Exception e) {
      return createErrorResponse(500, "Serialization error", e.getMessage());
    }
  }
  
  private APIGatewayProxyResponseEvent createErrorResponse(
      int statusCode, 
      String error, 
      String message) {
    
    Map<String, String> errorBody = new HashMap<>();
    errorBody.put("error", error);
    errorBody.put("message", message);
    errorBody.put("timestamp", Instant.now().toString());
    
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(statusCode)
        .withHeaders(getCorsHeaders())
        .withBody(toJson(errorBody));
  }
  
  private Map<String, String> getCorsHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Access-Control-Allow-Origin", "*"); // Configure appropriately
    headers.put("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    headers.put("Access-Control-Allow-Headers", "Content-Type, Authorization");
    return headers;
  }
}
```

##### 2. Update SAM Template

Add API Gateway resources to `iac/sam/template.yml`:

```yaml
Resources:
  # API Gateway
  VeriGateSanctionsApi:
    Type: AWS::Serverless::Api
    Properties:
      Name: !Sub "${AWS::StackName}-sanctions-api"
      StageName: !Ref EnvironmentShortname
      Auth:
        ApiKeyRequired: true
        UsagePlan:
          CreateUsagePlan: PER_API
          UsagePlanName: !Sub "${AWS::StackName}-usage-plan"
          Throttle:
            BurstLimit: 100
            RateLimit: 50
      Cors:
        AllowOrigin: "'*'"
        AllowMethods: "'POST, GET, OPTIONS'"
        AllowHeaders: "'Content-Type, Authorization'"
      DefinitionBody:
        openapi: "3.0.1"
        info:
          title: !Sub "${AWS::StackName}-sanctions-api"
          version: "1.0"
        paths:
          /api/v1/sanctions/match:
            post:
              summary: "Match entities against sanctions databases"
              requestBody:
                required: true
                content:
                  application/json:
                    schema:
                      $ref: "#/components/schemas/SanctionsMatchRequest"
              responses:
                "200":
                  description: "Successful match"
                "400":
                  description: "Invalid request"
                "503":
                  description: "Service unavailable"
              x-amazon-apigateway-integration:
                uri: !Sub "arn:aws:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${SanctionsMatchFunction.Arn}/invocations"
                httpMethod: POST
                type: aws_proxy

  # Lambda Function for Sanctions Match API
  SanctionsMatchFunction:
    Type: AWS::Serverless::Function
    Properties:
      FunctionName: !Sub "${AWS::StackName}-sanctions-match-api"
      Handler: verigate.adapter.opensanctions.api.handlers.SanctionsMatchApiHandler::handleRequest
      CodeUri: ../../src/verigate-adapter-opensanctions-api
      Role: !Sub "{{resolve:ssm:/application/iam-role/verigate-api-${EnvironmentShortname}/arn}}"
      Environment:
        Variables:
          OPENSANCTIONS_API_KEY: !Sub "{{resolve:ssm:/verigate/opensanctions/api-key}}"
          OPENSANCTIONS_BASE_URL: "https://api.opensanctions.org"
      Events:
        ApiEvent:
          Type: Api
          Properties:
            RestApiId: !Ref VeriGateSanctionsApi
            Path: /api/v1/sanctions/match
            Method: POST

  # Lambda Function for Sanctions Search API
  SanctionsSearchFunction:
    Type: AWS::Serverless::Function
    Properties:
      FunctionName: !Sub "${AWS::StackName}-sanctions-search-api"
      Handler: verigate.adapter.opensanctions.api.handlers.SanctionsSearchApiHandler::handleRequest
      CodeUri: ../../src/verigate-adapter-opensanctions-api
      Role: !Sub "{{resolve:ssm:/application/iam-role/verigate-api-${EnvironmentShortname}/arn}}"
      Environment:
        Variables:
          OPENSANCTIONS_API_KEY: !Sub "{{resolve:ssm:/verigate/opensanctions/api-key}}"
      Events:
        ApiEvent:
          Type: Api
          Properties:
            RestApiId: !Ref VeriGateSanctionsApi
            Path: /api/v1/sanctions/search
            Method: GET

  # Lambda Function for Health Check
  SanctionsHealthCheckFunction:
    Type: AWS::Serverless::Function
    Properties:
      FunctionName: !Sub "${AWS::StackName}-sanctions-health-api"
      Handler: verigate.adapter.opensanctions.api.handlers.SanctionsHealthCheckApiHandler::handleRequest
      CodeUri: ../../src/verigate-adapter-opensanctions-api
      Role: !Sub "{{resolve:ssm:/application/iam-role/verigate-api-${EnvironmentShortname}/arn}}"
      Environment:
        Variables:
          OPENSANCTIONS_API_KEY: !Sub "{{resolve:ssm:/verigate/opensanctions/api-key}}"
      Events:
        ApiEvent:
          Type: Api
          Properties:
            RestApiId: !Ref VeriGateSanctionsApi
            Path: /api/v1/sanctions/health
            Method: GET

Outputs:
  SanctionsApiUrl:
    Description: "API Gateway endpoint URL for sanctions screening"
    Value: !Sub "https://${VeriGateSanctionsApi}.execute-api.${AWS::Region}.amazonaws.com/${EnvironmentShortname}"
    Export:
      Name: !Sub "${AWS::StackName}-sanctions-api-url"
      
  SanctionsApiKey:
    Description: "API Key for sanctions API"
    Value: !Ref VeriGateSanctionsApiApiKey
```

##### 3. API Request/Response Models

**Request Model (Frontend → Backend):**

```json
{
  "dataset": "sanctions",
  "entity": {
    "schema": "Person",
    "name": "John Smith",
    "birthDate": "1980-05-15",
    "nationality": "US",
    "idNumber": "123456789"
  },
  "options": {
    "limit": 10,
    "threshold": 0.7,
    "topics": ["sanction", "role.pep"],
    "includeDatasets": ["us-ofac-sdn"],
    "excludeSchemas": ["Company"]
  }
}
```

**Response Model (Backend → Frontend):**

```json
{
  "requestId": "req_abc123xyz",
  "timestamp": "2025-01-15T10:30:45Z",
  "matches": [
    {
      "id": "entity_001",
      "score": 0.92,
      "caption": "John Michael Smith",
      "schema": "Person",
      "datasets": ["us-ofac-sdn", "eu-sanctions"],
      "properties": {
        "name": ["John Michael Smith"],
        "birthDate": ["1980-05-15"],
        "nationality": ["US"],
        "aliases": ["J. Smith", "Johnny Smith"]
      },
      "topics": ["sanction"],
      "reason": "Exact name and birth date match",
      "riskLevel": "HIGH"
    },
    {
      "id": "entity_002",
      "score": 0.68,
      "caption": "John R. Smith",
      "schema": "Person",
      "datasets": ["worldbank-debarred"],
      "properties": {
        "name": ["John Robert Smith"],
        "nationality": ["US"]
      },
      "topics": ["debarment"],
      "reason": "Partial name match",
      "riskLevel": "MEDIUM"
    }
  ],
  "metadata": {
    "totalMatches": 2,
    "processingTimeMs": 450,
    "datasetVersion": "2025-01-15"
  }
}
```

##### 4. Frontend Integration Examples

**React/TypeScript Example:**

```typescript
// src/services/sanctionsApi.ts
import axios, { AxiosInstance } from 'axios';

interface SanctionsMatchRequest {
  dataset: string;
  entity: {
    schema: 'Person' | 'Company' | 'Organization';
    name: string;
    birthDate?: string;
    nationality?: string;
    idNumber?: string;
  };
  options?: {
    limit?: number;
    threshold?: number;
    topics?: string[];
    includeDatasets?: string[];
    excludeSchemas?: string[];
  };
}

interface SanctionsMatch {
  id: string;
  score: number;
  caption: string;
  schema: string;
  datasets: string[];
  properties: Record<string, string[]>;
  topics: string[];
  reason: string;
  riskLevel: 'HIGH' | 'MEDIUM' | 'LOW';
}

interface SanctionsMatchResponse {
  requestId: string;
  timestamp: string;
  matches: SanctionsMatch[];
  metadata: {
    totalMatches: number;
    processingTimeMs: number;
    datasetVersion: string;
  };
}

class SanctionsApiService {
  private client: AxiosInstance;

  constructor(baseUrl: string, apiKey: string) {
    this.client = axios.create({
      baseURL: baseUrl,
      headers: {
        'Content-Type': 'application/json',
        'x-api-key': apiKey,
      },
      timeout: 30000,
    });
  }

  async matchEntity(request: SanctionsMatchRequest): Promise<SanctionsMatchResponse> {
    try {
      const response = await this.client.post<SanctionsMatchResponse>(
        '/api/v1/sanctions/match',
        request
      );
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          `Sanctions screening failed: ${error.response?.data?.message || error.message}`
        );
      }
      throw error;
    }
  }

  async searchEntities(query: string, dataset = 'sanctions', limit = 10): Promise<SanctionsMatchResponse> {
    try {
      const response = await this.client.get<SanctionsMatchResponse>(
        '/api/v1/sanctions/search',
        {
          params: { q: query, dataset, limit },
        }
      );
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          `Sanctions search failed: ${error.response?.data?.message || error.message}`
        );
      }
      throw error;
    }
  }

  async checkHealth(): Promise<boolean> {
    try {
      const response = await this.client.get('/api/v1/sanctions/health');
      return response.status === 200;
    } catch {
      return false;
    }
  }
}

export default SanctionsApiService;
```

**React Component Example:**

```tsx
// src/components/SanctionsScreening.tsx
import React, { useState } from 'react';
import SanctionsApiService from '../services/sanctionsApi';

const sanctionsApi = new SanctionsApiService(
  process.env.REACT_APP_API_URL!,
  process.env.REACT_APP_API_KEY!
);

export const SanctionsScreening: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  const handleScreening = async (formData: any) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await sanctionsApi.matchEntity({
        dataset: 'sanctions',
        entity: {
          schema: 'Person',
          name: formData.name,
          birthDate: formData.birthDate,
          nationality: formData.nationality,
        },
        options: {
          limit: 10,
          threshold: 0.7,
          topics: ['sanction', 'role.pep'],
        },
      });
      
      setResults(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="sanctions-screening">
      <h2>Sanctions Screening</h2>
      
      {/* Form components */}
      
      {loading && <div>Screening in progress...</div>}
      
      {error && <div className="error">{error}</div>}
      
      {results && (
        <div className="results">
          <h3>Screening Results</h3>
          <p>Found {results.metadata.totalMatches} potential matches</p>
          
          {results.matches.map((match: any) => (
            <div key={match.id} className={`match risk-${match.riskLevel.toLowerCase()}`}>
              <h4>{match.caption} (Score: {(match.score * 100).toFixed(1)}%)</h4>
              <p>Risk Level: {match.riskLevel}</p>
              <p>Reason: {match.reason}</p>
              <p>Datasets: {match.datasets.join(', ')}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
```

---

### Option 2: GraphQL API

For more flexible querying and better frontend developer experience.

#### Benefits
- ✅ Single endpoint for all operations
- ✅ Client-specified response shape
- ✅ Type safety with schema
- ✅ Reduces over-fetching
- ✅ Real-time subscriptions possible

#### Implementation
Use AWS AppSync or implement with Lambda + API Gateway:

```graphql
type Query {
  matchEntities(input: MatchEntitiesInput!): MatchEntitiesResponse!
  searchEntities(dataset: String!, query: String!, limit: Int): SearchEntitiesResponse!
  healthCheck: HealthStatus!
}

input MatchEntitiesInput {
  dataset: String!
  entity: EntityInput!
  options: MatchOptionsInput
}

input EntityInput {
  schema: EntitySchema!
  name: String!
  birthDate: String
  nationality: String
  idNumber: String
}

enum EntitySchema {
  PERSON
  COMPANY
  ORGANIZATION
}

type MatchEntitiesResponse {
  requestId: ID!
  matches: [SanctionsMatch!]!
  metadata: ResponseMetadata!
}

type SanctionsMatch {
  id: ID!
  score: Float!
  caption: String!
  schema: String!
  datasets: [String!]!
  properties: JSON!
  topics: [String!]!
  riskLevel: RiskLevel!
}

enum RiskLevel {
  HIGH
  MEDIUM
  LOW
}
```

---

### Option 3: WebSocket API (Real-time)

For real-time screening updates and long-running operations.

#### Use Cases
- Live screening status updates
- Batch processing with progress
- Real-time alerts for new sanctions

#### Implementation
```yaml
Resources:
  SanctionsWebSocketApi:
    Type: AWS::ApiGatewayV2::Api
    Properties:
      Name: !Sub "${AWS::StackName}-sanctions-websocket"
      ProtocolType: WEBSOCKET
      RouteSelectionExpression: "$request.body.action"
```

---

## Security Considerations

### Authentication Options

1. **API Keys** (Simple, recommended for MVP)
   - Generated per client
   - Included in `x-api-key` header
   - Managed through API Gateway

2. **AWS Cognito** (User authentication)
   - OAuth 2.0 / OpenID Connect
   - User pools for customer management
   - JWT tokens

3. **AWS IAM** (Service-to-service)
   - SigV4 signing
   - Fine-grained permissions
   - Best for backend integrations

### Authorization

```java
// Example: Role-based access control
public class AuthorizationMiddleware {
  
  public boolean canAccessSanctionsScreening(String userId, String operation) {
    // Check user permissions from Cognito groups or DynamoDB
    List<String> userRoles = getUserRoles(userId);
    
    return userRoles.contains("COMPLIANCE_OFFICER") || 
           userRoles.contains("ADMIN");
  }
  
  public boolean canAccessDataset(String userId, String dataset) {
    // Restrict certain datasets to specific user groups
    if (dataset.equals("us-ofac-sdn")) {
      return hasPermission(userId, "ACCESS_OFAC_DATA");
    }
    return true;
  }
}
```

### Rate Limiting

```yaml
Resources:
  SanctionsApiUsagePlan:
    Type: AWS::ApiGateway::UsagePlan
    Properties:
      UsagePlanName: !Sub "${AWS::StackName}-usage-plan"
      Throttle:
        BurstLimit: 100    # Maximum concurrent requests
        RateLimit: 50      # Requests per second
      Quota:
        Limit: 10000       # Monthly request limit
        Period: MONTH
```

### Data Protection

- **Encrypt in transit**: TLS 1.2+
- **Encrypt at rest**: DynamoDB encryption, S3 encryption
- **PII masking**: Mask sensitive data in logs
- **Audit logging**: All API calls logged to CloudWatch

---

## Performance Optimization

### Caching Strategy

```java
// Cache frequently screened entities
@Component
public class SanctionsCacheService {
  
  private final Cache<String, EntityMatchResponse> cache;
  
  public SanctionsCacheService() {
    this.cache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .maximumSize(10000)
        .build();
  }
  
  public EntityMatchResponse getOrScreen(
      EntityMatchRequest request,
      Supplier<EntityMatchResponse> screeningFunction) {
    
    String cacheKey = generateCacheKey(request);
    
    EntityMatchResponse cached = cache.getIfPresent(cacheKey);
    if (cached != null) {
      LOGGER.info("Cache hit for: " + cacheKey);
      return cached;
    }
    
    EntityMatchResponse result = screeningFunction.get();
    cache.put(cacheKey, result);
    
    return result;
  }
}
```

### API Gateway Caching

```yaml
Resources:
  VeriGateSanctionsApi:
    Type: AWS::Serverless::Api
    Properties:
      CacheClusterEnabled: true
      CacheClusterSize: "0.5"
      MethodSettings:
        - ResourcePath: /api/v1/sanctions/match
          HttpMethod: POST
          CachingEnabled: false  # Don't cache POST
        - ResourcePath: /api/v1/sanctions/search
          HttpMethod: GET
          CachingEnabled: true   # Cache GET requests
          CacheTtlInSeconds: 300
```

---

## Monitoring and Observability

### CloudWatch Metrics

```java
// Custom metrics for sanctions screening
public class SanctionsMetrics {
  
  private final CloudWatchAsyncClient cloudWatch;
  
  public void recordScreeningRequest(
      String dataset, 
      double scoreThreshold,
      int matchCount,
      long latencyMs) {
    
    PutMetricDataRequest request = PutMetricDataRequest.builder()
        .namespace("VeriGate/Sanctions")
        .metricData(
            MetricDatum.builder()
                .metricName("ScreeningLatency")
                .value((double) latencyMs)
                .unit(StandardUnit.MILLISECONDS)
                .dimensions(
                    Dimension.builder()
                        .name("Dataset")
                        .value(dataset)
                        .build()
                )
                .timestamp(Instant.now())
                .build(),
            MetricDatum.builder()
                .metricName("MatchCount")
                .value((double) matchCount)
                .unit(StandardUnit.COUNT)
                .build()
        )
        .build();
    
    cloudWatch.putMetricData(request);
  }
}
```

### X-Ray Tracing

Enable in SAM template:
```yaml
Globals:
  Function:
    Tracing: Active
```

---

## Cost Optimization

### Estimated Costs (Monthly)

| Component | Volume | Cost |
|-----------|--------|------|
| API Gateway | 1M requests | $3.50 |
| Lambda (256MB, 1s avg) | 1M invocations | $0.20 |
| CloudWatch Logs | 10GB | $5.00 |
| DynamoDB (on-demand) | 1M writes | $1.25 |
| Data Transfer | 100GB out | $9.00 |
| **Total** | | **~$19/month** |

### Cost Reduction Strategies

1. **Use Lambda SnapStart** (already enabled)
2. **Enable API Gateway caching** for GET endpoints
3. **Implement client-side caching** (5-15 minutes)
4. **Batch requests** when possible
5. **Use Reserved Capacity** for predictable workloads

---

## Testing Strategy

### Integration Tests

```java
@Test
void testSanctionsMatchApi() throws Exception {
  // Arrange
  APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
      .withBody("""
          {
            "dataset": "sanctions",
            "entity": {
              "schema": "Person",
              "name": "Vladimir Putin",
              "birthDate": "1952-10-07"
            }
          }
          """)
      .withHeaders(Map.of("x-api-key", testApiKey));
  
  // Act
  APIGatewayProxyResponseEvent response = 
      handler.handleRequest(request, mockContext);
  
  // Assert
  assertEquals(200, response.getStatusCode());
  
  SanctionsMatchResponse result = 
      MAPPER.readValue(response.getBody(), SanctionsMatchResponse.class);
  
  assertTrue(result.getMatches().size() > 0);
  assertTrue(result.getMatches().get(0).getScore() > 0.8);
}
```

### Load Testing

```bash
# Using Artillery
artillery quick --count 100 --num 10 \
  https://api.verigate.com/api/v1/sanctions/match \
  --header "x-api-key: ${API_KEY}" \
  --payload sanctions-test-payload.json
```

---

## Deployment Checklist

- [ ] Create API Lambda handlers
- [ ] Update SAM template with API Gateway resources
- [ ] Configure API keys and usage plans
- [ ] Set up CloudWatch alarms
- [ ] Enable X-Ray tracing
- [ ] Configure CORS policies
- [ ] Set up custom domain (optional)
- [ ] Document API endpoints (OpenAPI/Swagger)
- [ ] Create client SDKs
- [ ] Set up monitoring dashboards
- [ ] Implement rate limiting
- [ ] Configure WAF rules (optional)
- [ ] Set up automated tests
- [ ] Create runbooks for incidents

---

## Next Steps

1. **Phase 1**: Implement basic REST API with match and search endpoints
2. **Phase 2**: Add authentication (API keys)
3. **Phase 3**: Implement caching and optimization
4. **Phase 4**: Add monitoring and alerting
5. **Phase 5**: Create frontend SDK and documentation
6. **Phase 6**: Add GraphQL or WebSocket support (optional)

---

## Additional Resources

- [AWS API Gateway Documentation](https://docs.aws.amazon.com/apigateway/)
- [AWS Lambda Best Practices](https://docs.aws.amazon.com/lambda/latest/dg/best-practices.html)
- [OpenAPI Specification](https://swagger.io/specification/)
- [AWS SAM Documentation](https://docs.aws.amazon.com/serverless-application-model/)
