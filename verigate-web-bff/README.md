# Verigate Web BFF

A Spring Boot based backend-for-frontend (BFF) that provides synchronous HTTP endpoints for
submitting verification requests from the web client and querying command execution state. The
service projects the command-gateway capabilities to REST while delegating all provider routing and
processing to the existing command gateway infrastructure.

## Features

- `POST /api/verifications` – accepts verification submissions, validates using the shared
  `VerifyPartyCommand` specifications, and dispatches the command to the mapped adapter queue.
- `GET /api/verifications/{id}` – surfaces current status by reading from the command store
  DynamoDB table managed by the command gateway.
- Uses Spring Boot, AWS SDK v2 (SQS + DynamoDB enhanced client), and the shared kernel/
  command-gateway domain/application modules for consistency in validation and serialization.

## Local Development

1. **Configure environment variables**:
   ```bash
   cp .env.example .env.local
   ```
   Edit `.env.local` with your AWS and service configuration. See `.env.example` for detailed
   documentation of all available variables.

2. Ensure the command gateway modules are installed locally so the shared domain artifacts are
   available:
   ```bash
   mvn -pl src/verigate-command-gateway/verigate-command-gateway-domain,src/verigate-command-gateway/verigate-command-gateway-application install
   ```

3. From this directory build and run the BFF:
   ```bash
   mvn spring-boot:run
   ```

For a complete list of environment variables and their defaults, see `.env.example`.

## Configuration

Configuration is provided via `application.yaml` and overridable environment variables. See
`.env.example` for a complete reference of all available environment variables with defaults and
descriptions. Queue mappings are kept external so providers can be re-routed without code changes.
