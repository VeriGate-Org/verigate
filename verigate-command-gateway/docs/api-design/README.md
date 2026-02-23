# API Design Documentation

This directory contains API design documentation for exposing VeriGate services to frontend applications.

## 📚 Available Documents

### 1. [BFF Microservice Design](./bff-microservice-design.md) ⭐ **RECOMMENDED**
Complete design for a standalone Backend-for-Frontend (BFF) microservice using Spring Boot and DDD principles.

**Key Features:**
- Clean architecture with DDD patterns (Aggregates, Value Objects, Domain Services)
- Aligns perfectly with VeriGate's existing hexagonal architecture
- Spring Boot 3.x on AWS ECS Fargate
- Integration with existing Command Gateway and Kinesis events
- WebSocket support for real-time updates
- Comprehensive domain modeling examples

**Best For:**
- Production applications with rich domain logic
- Teams familiar with Spring Boot
- Applications requiring complex orchestration
- When predictable traffic justifies fixed costs

---

### 2. [OpenSanctions Frontend API (Lambda-based)](./opensanctions-frontend-api.md)
Alternative approach using AWS API Gateway + Lambda for simpler, serverless deployment.

**Key Features:**
- Serverless architecture (pay-per-request)
- Direct Lambda handlers
- Lower operational complexity
- Detailed frontend integration examples (React/TypeScript)

**Best For:**
- Simple request/response patterns
- Highly variable traffic
- Cost optimization as primary concern
- MVP or proof-of-concept

---

## 🏗️ Architecture Comparison

| Aspect | BFF Microservice | Lambda API |
|--------|------------------|------------|
| **Architecture Pattern** | Hexagonal/Clean (DDD) | Serverless Functions |
| **Framework** | Spring Boot 3.x | AWS Lambda Runtime |
| **Deployment** | ECS Fargate (Container) | Serverless |
| **Domain Logic** | ✅ Rich domain modeling | ⚠️ Limited |
| **State Management** | ✅ In-memory + Redis | ❌ External only |
| **WebSocket Support** | ✅ Native Spring WebSocket | ⚠️ API Gateway WebSocket |
| **Development DX** | ✅ Traditional Spring Boot | ⚠️ AWS-specific |
| **Local Testing** | ✅ Easy (Docker Compose) | ⚠️ LocalStack required |
| **Cost (1M req/month)** | ~$95 | ~$19 |
| **Response Time (p50)** | < 100ms | < 500ms |
| **Response Time (p99)** | < 300ms | < 5s (cold starts) |
| **Scalability** | ✅ Auto-scaling | ✅ Auto-scaling |

## 🎯 Decision Guide

### Choose BFF Microservice When:

✅ You have **rich domain logic** and complex business rules  
✅ You want **perfect DDD/Clean Architecture alignment**  
✅ You need **request orchestration** and state management  
✅ You require **WebSocket** for real-time updates  
✅ Your traffic is **predictable** (thousands+ requests/day)  
✅ Team is **familiar with Spring Boot**  
✅ You value **superior developer experience**  

### Choose Lambda API When:

✅ You have **simple request/response** patterns  
✅ You want **minimal operational overhead**  
✅ Traffic is **highly variable or unpredictable**  
✅ **Cost optimization** is the primary concern  
✅ You're building an **MVP or proof-of-concept**  
✅ You have **limited domain complexity**  

## 💡 Recommended Hybrid Approach

For VeriGate, we recommend a **hybrid architecture**:

```
┌─────────────┐
│  Frontend   │
└──────┬──────┘
       │ HTTPS
       ▼
┌─────────────────────────────────────┐
│    BFF Microservice (Spring Boot)   │  ← Frontend-facing API
│  • REST API endpoints                │  ← Domain logic
│  • WebSocket for real-time updates   │  ← State management
│  • Request validation & aggregation  │  ← Caching
└──────┬──────────────────────────────┘
       │ SQS
       ▼
┌─────────────────────────────────────┐
│  Command Gateway (Lambda - Existing)│  ← Command routing
│  • Queue-based processing            │  ← Cost-effective
│  • Event publishing to Kinesis       │  ← Async processing
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  Adapters (Lambda - Existing)       │  ← External integrations
│  • OpenSanctions, WorldCheck, etc.  │  ← API calls
└─────────────────────────────────────┘
```

**Benefits:**
- ✅ **BFF**: Superior frontend DX and domain modeling
- ✅ **Lambda**: Cost-effective backend processing
- ✅ **Best of both worlds**: Quality + Efficiency

## 📋 Implementation Checklist

### BFF Microservice Path

- [ ] Review [BFF design document](./bff-microservice-design.md)
- [ ] Set up Spring Boot project structure
- [ ] Implement domain models (Aggregates, Value Objects)
- [ ] Create application layer (Use Cases)
- [ ] Implement infrastructure layer (SQS, Kinesis, DynamoDB, Redis)
- [ ] Set up ECS Fargate deployment
- [ ] Configure Application Load Balancer
- [ ] Implement authentication (JWT/API Keys)
- [ ] Set up monitoring and observability
- [ ] Deploy to production

### Lambda API Path

- [ ] Review [Lambda API design document](./opensanctions-frontend-api.md)
- [ ] Create Lambda handler functions
- [ ] Update SAM template with API Gateway
- [ ] Configure API Keys and usage plans
- [ ] Implement request/response models
- [ ] Set up CloudWatch monitoring
- [ ] Create frontend SDK
- [ ] Deploy and test

## 🚀 Quick Start

### For BFF Microservice:

```bash
# Clone and set up project
cd verigate-bff
./mvnw clean install

# Run locally with Docker Compose
docker-compose up

# Test the API
curl -X POST http://localhost:8080/api/v1/screenings \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"type": "SANCTIONS_SCREENING", "entity": {...}}'
```

### For Lambda API:

```bash
# Build and deploy with SAM
cd iac/sam
sam build
sam deploy --guided
```

## 📊 Cost Breakdown

### BFF Microservice (1M requests/month)
- ECS Fargate (2 tasks, 24/7): **$50**
- Application Load Balancer: **$16**
- Redis (ElastiCache): **$12**
- DynamoDB + Logs + Data Transfer: **$16**
- **Total: ~$94/month**

### Lambda API (1M requests/month)
- API Gateway: **$3.50**
- Lambda invocations: **$0.20**
- CloudWatch + DynamoDB + Data Transfer: **$15.30**
- **Total: ~$19/month**

## 📖 Additional Resources

- [VeriGate Architecture Overview](../../README.md)
- [OpenSanctions Integration Guide](../../src/verigate-adapter-opensanctions/CLAUDE.md)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [Domain-Driven Design Reference](https://www.domainlanguage.com/ddd/)

## 🤝 Contributing

For questions, suggestions, or contributions:
1. Review the architecture documents
2. Discuss with the VeriGate development team
3. Follow the established patterns and conventions
4. Submit changes for review

---

**Last Updated:** January 2025  
**Maintained By:** VeriGate Development Team
